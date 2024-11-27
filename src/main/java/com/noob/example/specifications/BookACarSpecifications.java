package com.noob.example.specifications;
import com.noob.example.entity.BookACar;
import com.noob.example.enums.BookCarStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import java.util.Date;

public class BookACarSpecifications {

    // Tìm kiếm theo email người dùng
    public static Specification<BookACar> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) return null;
            Join<Object, Object> userJoin = root.join("user", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    // Tìm kiếm theo tên người dùng
    public static Specification<BookACar> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) return null;
            Join<Object, Object> userJoin = root.join("user", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), "%" + username.toLowerCase() + "%");
        };
    }

    // Tìm kiếm theo trạng thái đặt xe
    public static Specification<BookACar> hasStatus(BookCarStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("bookCarStatus"), status);
    }

    // Tìm kiếm theo  thời gian (fromDate và toDate)
    public static Specification<BookACar> betweenDates(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null || toDate == null) return null;
            return criteriaBuilder.between(root.get("fromDate"), fromDate, toDate);
        };
    }

    // Tìm kiếm theo giá
    public static Specification<BookACar> betweenPrice(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null || maxPrice == null) return null;
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }

    // Tìm kiếm theo mã xe (carId)
    public static Specification<BookACar> hasCarId(Long carId) {
        return (root, query, criteriaBuilder) ->
                carId == null ? null : criteriaBuilder.equal(root.join("car").get("id"), carId);
    }
}
