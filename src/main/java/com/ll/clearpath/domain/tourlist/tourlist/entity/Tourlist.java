package com.ll.clearpath.domain.tourlist.tourlist.entity;

import com.ll.clearpath.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class Tourlist extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String address;

    private String introduction;

    private double latitude;

    private double longitude;

    @OneToMany(mappedBy = "tourlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KeywordList> keywordLists;

}
