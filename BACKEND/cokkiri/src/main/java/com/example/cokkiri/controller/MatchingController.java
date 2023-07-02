package com.example.cokkiri.controller;


import com.example.cokkiri.model.*;

import com.example.cokkiri.service.MatchingService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.cokkiri.model.ClassMatchedList;
import com.example.cokkiri.model.ClassMatching;
import com.example.cokkiri.model.PublicMatchedList;
import com.example.cokkiri.model.PublicMatching;
import com.example.cokkiri.service.MatchingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("matching")
public class MatchingController {
    @Autowired
    private  MatchingService matchingService;


    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/free")
    public ResponseEntity<PublicMatchedList> publicMatch(@RequestBody PublicMatching user){
        System.out.println(user.getMatchingType());
        if(user.getMatchingType().equals("free")){
            return new ResponseEntity<>(matchingService.PublicMatch(user), HttpStatus.OK);
        }
        else{
            System.out.println("잘못된 송출");
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
}


    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/free")
    public  ResponseEntity<PublicMatchedList>publicMatch(@RequestBody PublicMatching user){
        if(user.getMatchingType().equals("free")) {
            return  new ResponseEntity<>(matchingService.PublicMatch(user), HttpStatus.OK);
        }

        return null;
    }



    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/class")
    public ResponseEntity<ClassMatchedList> classMatch(@RequestBody ClassMatching user){
        if(user.getMatchingType().equals("free")) {
            return  new ResponseEntity<>(matchingService.ClassMatch(user), HttpStatus.OK);
        }
        return null;



    }
}





