package com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.jpa;

import com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.Post;
import com.vladmihalcea.book.hpjp.util.AbstractTest;
import org.hibernate.integrator.spi.Integrator;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Mihalcea
 */
public class JPADTOProjectionImportTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
            Post.class,
        };
    }

    @Override
    protected Integrator integrator() {
        return DTOImportIntegrator.INSTANCE;
    }

    @Override
    public void afterInit() {
        doInJPA(entityManager -> {
            Post post = new Post();
            post.setId(1L);
            post.setTitle("High-Performance Java Persistence");
            post.setCreatedBy("Vlad Mihalcea");
            post.setCreatedOn(Timestamp.from(
                LocalDateTime.of(2016, 11, 2, 12, 0, 0).toInstant(ZoneOffset.UTC)
            ));
            post.setUpdatedBy("Vlad Mihalcea");
            post.setUpdatedOn(Timestamp.from(
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
            ));

            entityManager.persist(post);
        });
    }

    @Test
    public void testConstructorExpression() {
        doInJPA(entityManager -> {
            List<PostDTO> postDTOs = entityManager.createQuery(
                "select new PostDTO(p.id, p.title) " +
                "from Post p " +
                "where p.createdOn > :fromTimestamp", PostDTO.class)
            .setParameter("fromTimestamp", Timestamp.from(
                LocalDateTime.of(2016, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)))
            .getResultList();

            assertEquals(1, postDTOs.size());
        });
    }
}
