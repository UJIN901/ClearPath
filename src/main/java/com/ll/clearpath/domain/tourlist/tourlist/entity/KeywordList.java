package com.ll.clearpath.domain.tourlist.tourlist.entity;

import com.ll.clearpath.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class KeywordList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tourlist_id")
    private Tourlist tourlist;

    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
}
