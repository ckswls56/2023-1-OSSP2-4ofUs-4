import axios from 'axios';

// baseURL을 통합 관리하기 위해 생성한 axios 객체
const instance = axios.create({
	// Spring Boot 서버의 주소와 포트를 입력
	baseURL: 'http://3.37.37.164:8080'
});

export default instance;