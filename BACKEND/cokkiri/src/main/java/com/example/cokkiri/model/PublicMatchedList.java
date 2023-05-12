package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PublicMatchedList {
    //매칭 결과에 대한 Entity

    //매칭 번호 (auto increment)
    @Id
    @GeneratedValue()
    @Column
    private int matchingId;

    //희망인원
    @Column
    private int headCount;

    //매칭된 시간
    @Column
    private LocalDate matchingTime;

    //매칭된 사람들 학번
    @ElementCollection
    private List<String> studentIdList;

    @Column
    //매칭 가능한 요일
    private  LocalDate availableDay;

    //약속시간
    @Column
    @ElementCollection
    private List<LocalTime> promiseTime;

    @Column
    @ColumnDefault("0")
    //매칭 동의수 사람수랑 같게 되면 matchingRes=true
    private int matchingAgree;

    @Column
    @ColumnDefault("false")
    //매칭결과
    private boolean matchingRes;

    @Column
    //매칭타입
    //공강=free , 수업=class
    private  String matchingType;

}

