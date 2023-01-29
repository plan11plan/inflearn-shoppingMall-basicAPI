package jpabook.jpashop.service;

import static org.junit.Assert.*;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Test
    @DisplayName("상품주문")
    public void 상품주문() throws Exception{
        //given
        Member member = new Member(); //이름 주소
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);

        Book book = new Book(); //Item ->이름,가격,수량 Book-> 저자,isbn
        book.setName("김영한 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        book.setAuthor("김영한");
        book.setIsbn("sj");
        em.persist(book);

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), 2);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER,getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다.", 1,getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000*2,getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 8,book.getStockQuantity());



    }

    @Test
    @DisplayName("상품취소")
    public void 상품취소() throws Exception{
        //given
        Member member = new Member(); //이름 주소
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);

        Book book = new Book(); //Item ->이름,가격,수량 Book-> 저자,isbn
        book.setName("김영한 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        book.setAuthor("김영한");
        book.setIsbn("sj");
        em.persist(book);

        int orderCount =2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문  취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.",10,book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    @DisplayName("상품주문 재고수량초과")
    public void 상품주문_재고수량초과()throws Exception{
        //given
        Member member = new Member(); //이름 주소
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);

        Book book = new Book(); //Item ->이름,가격,수량 Book-> 저자,isbn
        book.setName("김영한 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        book.setAuthor("김영한");
        book.setIsbn("sj");
        em.persist(book);

        int orderCount =11;
        //when
        orderService.order(member.getId(), book.getId(),orderCount);
        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");

    }

}