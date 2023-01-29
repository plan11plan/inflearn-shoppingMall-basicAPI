package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    //주문한사람 //상품명 //개수 //배달상태 //주문날짜
    @Id @GeneratedValue
    @Column(name ="order_id")
    private long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL) //Cascade=>자식까지 한번에 persist
    private List<OrderItem> orderItems = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)//Cascade=>자식까지 한번에 persist
    @JoinColumn(name ="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 ORDER,CANCEL

    //==연관관계 메서드==//
    public void setMember(Member member){
        this.member=member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery){
        this.delivery=delivery;
        delivery.setOrder(this) ;
    }
    //== 생성 메서드 ==//
    public static Order createOrder(Member member,Delivery delivery,OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);  //주문 멤버
        order.setDelivery(delivery);  //주문 배달
        for(OrderItem orderItem: orderItems){ //주문 아이템
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);  //주문 상태
        order.setOrderDate(LocalDateTime.now());//주문 시간
        return order;
    }
    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        if(delivery.getDeliveryStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
    //== 조회 로직 ==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice(){
        int totalPrice =0;
        for(OrderItem orderItem : orderItems){
            totalPrice+= orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
