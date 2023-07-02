package com.example.cokkiri.service;

import com.example.cokkiri.controller.MatchingController;
import com.example.cokkiri.model.*;
import com.example.cokkiri.repository.MatchedListRepository;
import com.example.cokkiri.repository.PublicMatchedListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service("service")
public class MatchingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingService.class);
    // 싱글스레드 호출
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 수업 레포지토리
    @Autowired
    private MatchedListRepository classMatchedListRepository;

    // 공강 레포지토리
    @Autowired
    private PublicMatchedListRepository publicMatchedListRepository;
    // 배열에 저장 (공강)
    List<PublicMatching> publicLectureUsers = new ArrayList<>();
    // 배열에 저장 (수업)
    List<ClassMatching> classLectureUsers = new ArrayList<>();
    //반환 배열
    List<PublicMatching> publicUsersList = new ArrayList<>();
    List<ClassMatching> classUserList =new ArrayList<>();


    // 요일, 시간, 희망인원이 같을 시 증가
    int userCount;
    //매치된 user들 지우는 용도
    List<Integer> usermatched = new ArrayList<>();



    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }
    // 겹치는 학수번호 확인
    public static Set<String> findDuplicatesCourse(List<String>List) {

    // 겹치는 시간대 확인
    public static Set<String> findDuplicates(List<String>List) {

        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (String i: List) {
            if (!seen.add(i)) {
                duplicates.add(i);
            }
        }
        return duplicates;
    }



    public PublicMatchedList findPublicMatch(List<PublicMatching>userList , int count ){
        if(userList.size()<2){
            return null;
        }
        else{
            // 객체 생성
            PublicMatchedList matched = new PublicMatchedList();

            for(int i =0; i <= userList.size()-2; i++){
                    List<String>firstUserTime = userList.get(i).getPromiseTime();
                    List<String>lastUserTime = userList.get(userList.size()-1).getPromiseTime();
                    List<String>timeList = new ArrayList<>();
                    timeList.addAll(firstUserTime);
                    timeList.addAll(lastUserTime);
                    //약속시간이 겹치는 times를 찾아
                    List<String> times = new ArrayList<>(findDuplicates(timeList));
                    System.out.println("겹치는 시간대는 : " + times + " 입니다");

                    // 마지막 요소와 시간요일,희망인원이 같은 요소있으면 배열 다 돌아

                    boolean day = (userList.get(i).getAvailableDay()).equals(userList.get(userList.size()-1).getAvailableDay());
                    boolean head = (userList.get(i).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                    if (day&&head&&times!=null) {

                        userCount+=1;
                        // 요소를 찾았지만 희망인원이 채워 졌는지 묻는 조건문
                        if(userCount+1==count){
                            PublicMatching userLast = userList.get(userList.size() - 1);

                            // 반환 배열에 넣음
                            publicUsersList.add(userLast);



                            for(int j = 0; j < userList.size()-1 ; j++){
                                List<String>first = userList.get(j).getPromiseTime();
                                List<String>last = userList.get(userList.size()-1).getPromiseTime();
                                List<String>timesLists = new ArrayList<>();
                                timesLists.addAll(first);
                                timesLists.addAll(last);
                                //약속시간이 겹치는 times를 찾아
                                List<String> timesMatchLists = new ArrayList<>(findDuplicates(timesLists));
                                System.out.println("겹치는 시간대는 : " + timesMatchLists + " 입니다");
                                boolean days = (userList.get(j).getAvailableDay()).equals(userList.get(userList.size()-1).getAvailableDay());
                                boolean heads = (userList.get(j).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                                if (days&&heads&&timesMatchLists!=null) {

                                    // 겹치는 시간 확인
                                    matched.setPromiseTime(timesMatchLists);

                                    publicUsersList.add(userList.get(j));
                                    usermatched.add(j);

                                }
                            }
                            //마지막 요소 제거
                            userList.remove(userLast);
                            //매치 된 요소 제거
                            for(int k=0 ;  k <  usermatched.size() ;k++){
                                userList.remove(usermatched.get(k));
                            }
                            userCount =0;

                            // 학번 배열 생성, set
                            List<String> studentIdList = new ArrayList<>();
                            for (int k = 0; k <= publicUsersList.size() - 1; k++) {
                                String studentId = publicUsersList.get(k).getStudentId();
                                studentIdList.add(studentId);
                            }
                            // 매칭된 학생들 학번 리스트
                            matched.setStudentIdList(studentIdList);
                            // 매칭 타입
                            matched.setMatchingType(userLast.getMatchingType());
                            // 매칭 가능한 요일
                            matched.setAvailableDay(userLast.getAvailableDay());
                            // 매칭 희망인원
                            matched.setHeadCount(userLast.getHeadCount());

                            //매칭 시간 현재 날짜로 세팅
                            String pattern = "yyyy-MM-dd";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            String date = simpleDateFormat.format(new Date());

                            // 매칭 시간
                            matched.setMatchingTime(date);

                            publicUsersList.clear();

                            // entity 반환.
                            return matched;
                        }
                        //희망인원이 다 안채워 졌으면 continue
                        else{
                            continue;
                        }

                        userCount = 0;

                        // 학번 배열 생성, set
                        List<String> emailList = new ArrayList<>();
                        for (int k = 0; k <= publicUsersList.size() - 1; k++) {
                            String email = publicUsersList.get(k).getEmail();
                            emailList.add(email);
                        }
                        // 매칭된 학생들 이메일 리스트
                        matched.setEmailList(emailList);
                        // 매칭 타입
                        matched.setMatchingType(userLast.getMatchingType());
                        // 매칭 가능한 요일
                        matched.setAvailableDay(userLast.getAvailableDay());
                        // 매칭 희망인원
                        matched.setHeadCount(userLast.getHeadCount());

                        //매칭 시간 현재 날짜로 세팅
                        LocalDate date = LocalDate.now();
                        // 매칭 시간
                        matched.setMatchingTime(date);

                        publicUsersList.clear();

                        // entity 반환.
                        return matched;
                    }
                    //희망인원이 다 안채워 졌으면 continue
                    else {
                        continue;
                    }
                    //배열 내 요소가 다를 시,
                } else {
                    continue;

                        // 배열 내 요소가 다를 시,
                        }else{
                            continue;
                        }

                }
            //끝까지 돌았는데 못찾았을 시
            userCount=0;
            return null;
            }
    }


    //수업매칭
    public ClassMatchedList findClassMatch(List<ClassMatching>userList , int count ){
        // 객체 생성
        ClassMatchedList matched = new ClassMatchedList();
        if(userList.size()<2){
            return null;
        }
        else{


            for(int i =0; i <= userList.size()-2; i++){
                List<String>firstUserCourseNum = userList.get(i).getCourseNumber();
                List<String>lastUserCourseNum = userList.get(userList.size()-1).getCourseNumber();
                List<String>courseNumList = new ArrayList<>();
                courseNumList.addAll(firstUserCourseNum);
                courseNumList.addAll(lastUserCourseNum);
                //시간표가 겹치는 유저 찾아
                List<String> courseNum = new ArrayList<>(findDuplicates(courseNumList));
                System.out.println("겹치는 수업은 : " + courseNum + " 입니다");

                // 마지막 요소와 시간요일,희망인원이 같은 요소있으면 배열 다 돌아

                boolean head = (userList.get(i).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                if (head&&courseNum!=null) {

                    userCount+=1;
                    // 요소를 찾았지만 희망인원이 채워 졌는지 묻는 조건문
                    if(userCount+1==count){
                        ClassMatching userLast = userList.get(userList.size() - 1);

                        // 반환 배열에 넣음
                        classUserList.add(userLast);



                        for(int j = 0; j < userList.size()-1 ; j++){
                            List<String>firstUserCourseNumber = userList.get(i).getCourseNumber();
                            List<String>lastUserCourseNumber = userList.get(userList.size()-1).getCourseNumber();
                            List<String>courseNumberList = new ArrayList<>();
                            courseNumberList.addAll(firstUserCourseNumber);
                            courseNumberList.addAll(lastUserCourseNumber);
                            //시간표가 겹치는 유저 찾아
                            List<String> courseNumber = new ArrayList<>(findDuplicates(courseNumberList));
                            boolean heads = (userList.get(j).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                            if (heads&&courseNumber!=null) {

                                // 겹치는 시간 확인
                                matched.setCourseNumber(courseNumber);

                                classUserList.add(userList.get(j));
                                usermatched.add(j);

                            }
                        }
                        //마지막 요소 제거
                        userList.remove(userLast);
                        //매치 된 요소 제거
                        for(int k=0 ;  k <  usermatched.size() ;k++){
                            userList.remove(usermatched.get(k));
                        }
                        userCount =0;

                        // 학번 배열 생성, set
                        List<String> emailList = new ArrayList<>();
                        for (int k = 0; k <= classUserList.size() - 1; k++) {
                            String email = classUserList.get(k).getEmail();
                            emailList.add(email);
                        }
                        // 매칭된 학생들 학번 리스트
                        matched.setEmailList(emailList);
                        // 매칭 타입
                        matched.setMatchingType(userLast.getMatchingType());

                        // 매칭 희망인원
                        matched.setHeadCount(userLast.getHeadCount());

                        //매칭 시간 현재 날짜로 세팅
                        String pattern = "yyyy-MM-dd";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String date = simpleDateFormat.format(new Date());

                        // 매칭 시간
                        matched.setMatchingTime(date);

                        classUserList.clear();

                        // entity 반환.
                        return null;
                    }
                    //희망인원이 다 안채워 졌으면 continue
                    else{
                        continue;
                    }
                    // 배열 내 요소가 다를 시,
                }else{
                    continue;
                }
            }
            //끝까지 돌았는데 못찾았을 시
            userCount=0;
            return matched;
        }
    }
    @Autowired
  NotificationService notificationService;
    public PublicMatchedList PublicMatch(PublicMatching user) {

        // 매칭된 사람 수 = 희망인원
        int count = user.getHeadCount();
        publicLectureUsers.add(user);
        PublicMatchedList publicMatchedList = new PublicMatchedList();
        publicMatchedList = findPublicMatch(publicLectureUsers, count);
        if (publicMatchedList != null) {
            publicMatchedListRepository.save(publicMatchedList); // 데베에 저장
            PublicMatchedList finalPublicMatchedList = publicMatchedList;
            notificationService.sendNotificationToUser(finalPublicMatchedList.getEmailList(), "매칭이 완료되었습니다");
            }else{
                return null;
            }
            return  publicMatchedList;
        }

    public ClassMatchedList ClassMatch(ClassMatching user){
        // 매칭된 사람 수 = 희망인원
        int count = user.getHeadCount();
        classLectureUsers.add(user);
        ClassMatchedList classMatchedList = new ClassMatchedList();
        classMatchedList = findClassMatch(classLectureUsers,count);

        if (classMatchedList!=null){
            classMatchedListRepository.save(classMatchedList); // 데베에 저장
            ClassMatchedList finalClassMatchedList = classMatchedList;
            notificationService.sendNotificationToUser(finalClassMatchedList.getEmailList(), "매칭이 완료되었습니다");
        }else{
            return null;
        }
        return  classMatchedList;
    }

    //수업 매칭 전부 반환
    public List<ClassMatchedList> findAllClassMatching(){
        List<ClassMatchedList> matchedlist = new ArrayList<>();
        classMatchedListRepository.findAll().forEach(e->matchedlist.add(e));
        return matchedlist;
    }

    //공강 매칭 전부 반환
    public List<PublicMatchedList> findAllPublicMatching(){
        List<PublicMatchedList> matchedlist = new ArrayList<>();
        publicMatchedListRepository.findAll().forEach(e->matchedlist.add(e));
        return matchedlist;
    }

}
