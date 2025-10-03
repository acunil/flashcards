package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@MappedSuperclass
@SuperBuilder
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  private Long id;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;

    if (!(o instanceof BaseEntity other)) return false;

    Class<?> thisEffectiveClass =
        (this instanceof HibernateProxy proxy)
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();

    Class<?> otherEffectiveClass =
        (o instanceof HibernateProxy proxy)
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();

    if (!thisEffectiveClass.equals(otherEffectiveClass)) return false;

    return getId() != null && Objects.equals(getId(), other.getId());
  }

  @Override
  public final int hashCode() {
    return (this instanceof HibernateProxy proxy)
        ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
