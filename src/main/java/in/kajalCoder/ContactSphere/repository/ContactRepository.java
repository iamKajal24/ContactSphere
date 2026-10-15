package in.kajalCoder.ContactSphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.kajalCoder.ContactSphere.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

  // for contact delete
  Optional<Contact> findByIdAndUser_UserId(
      Long contactId,
      Long userId);

  // for view all contact List
  List<Contact> findByUser_UserId(Long userId);

  // Logged-in user ke sirf favorite contacts fetch karega
  List<Contact> findByUser_UserIdAndFavoriteTrue(Long userId);

  // Database-level search + favorite filter + pagination.
  @Query(value = """
      SELECT c
      FROM Contact c
      WHERE c.user.userId = :userId
        AND (:favoritesOnly = false OR c.favorite = true)
        AND (
            :keyword = ''
            OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.phoneNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
      """, countQuery = """
      SELECT COUNT(c)
      FROM Contact c
      WHERE c.user.userId = :userId
        AND (:favoritesOnly = false OR c.favorite = true)
        AND (
            :keyword = ''
            OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.phoneNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
      """)
  Page<Contact> searchContacts(
      @Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("favoritesOnly") boolean favoritesOnly,
      Pageable pageable);

  // Summary counts database se directly.
  long countByUser_UserId(Long userId);

  long countByUser_UserIdAndFavoriteTrue(Long userId);

}
