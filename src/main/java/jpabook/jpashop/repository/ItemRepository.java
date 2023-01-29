package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;
    /** 아이템 저장 */
    public void save(Item item) {
        //잠깐 검증. item은 처음에 id가 없다.
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }
    /** 아이템 단건조회 */
    public Item findOne(Long id){
        return em.find(Item.class,id);
    }
    /** 아이템 전체조회 */
    public List<Item> findAll(){
        return em.createQuery("select i from Item i")
                .getResultList();
    }
}
