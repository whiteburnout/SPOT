import axios from "../../axios/axios-common";

const CourseStore = {
  namespaced: true,
  state: {
    courseList: [],
    courseInfo: [
      {
        course_id: "1",
        spot_id: "1",
        latitude: "37.570425",
        longitude: "126.991316",
        name: "스팟명",
        course_name: "기본코스"
      },
      {
        course_id: "1",
        spot_id: "2",
        latitude: "37.571525",
        longitude: "126.991316",
        name: "두번째 스팟",
        course_name: "기본코스"
      }
    ]
  },
  getters: {
    getCourseList(state) {
      //멤버의 코스리스트 받아옴
      return state.courseList;
    },
    getCourseInfo(state) {
      //코스의 상세정보 받아오기
      return state.courseInfo;
    }
  },
  mutations: {
    setCourseList(state, payload) {
      state.courseList = payload;
    },
    setCourseInfo(state, payload) {
      state.courseInfo = payload;
    }
  },
  actions: {
    //회원 코스 이력 조회
    reqCourseList(context, user_id) {
      return axios
        .get("/courselist/" + user_id)
        .then(response => {
          if (response.data.message == "course list") {
            context.commit("setCourseList", response.data.result);
            return true;
          } else return false;
        })
        .catch(error => {
          console.log(error);
        });
    },
    //코스 상세정보 조회
    reqCourseInfo(context, fd) {
      return axios
        .get("/course/" + fd.user_id + "?course_id=" + fd.course_id)
        .then(response => {
          console.log(response.data);
          if (response.data.message == "course list info") {
            context.commit("setCourseInfo", response.data.result); //여기서 코스 정보 store에 넣어줌.
            return true;
          } else return false;
        })
        .catch(error => {
          console.log(error);
        });
    },

    //코스 생성
    reqCreateCourse(context, fd) {
      return axios
        .post("/course", {
          spotid: fd.spot_id,
          email: fd.email,
          orders: fd.orders
        })
        .then(response => {
          console.log(response.data);
          if (response.message == "success") {
            return true;
            //context.commit("setCourseInfo", response.data.course);
          } else return false;
        })
        .catch(error => {
          console.log(error);
        });
    },
    //코스 삭제
    reqDeleteCourse(context, course_id) {
      return axios
        .delete("/course/" + course_id)
        .then(response => {
          if (response.message == "success") {
            return true;
          } else return false;
        })
        .catch(error => {
          console.log(error);
          return false;
        });
    }
  }
};

export default CourseStore;
