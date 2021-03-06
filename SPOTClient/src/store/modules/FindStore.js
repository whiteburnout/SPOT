import axios from "axios";

const FindStore = {
  namespaced: true, // 모듈 개발사용 가능
  state: {
    isSearch: false,
    searchList: {},
    suggestList: [
      {
        spot_id: "500001",
        name: "관광지",
        latitude: "33.451705",
        longitude: "126.571677",
        img: ""
      },
      {
        spot_id: "1",
        name: "좋은 장소",
        lat: "33.450705",
        lng: "126.570677",
        img: ""
      }
    ]
  },

  getters: {
    getIsSearched(state) {
      return state.isSearch;
    },
    getSearchList(state) {
      return state.searchList;
    },
    getSuggestList(state) {
      return state.suggestList;
    }
  },
  mutations: {
    setIsSearched(state, isSearch) {
      state.isSearch = isSearch;
    },
    setSearchList(state, searchList) {
      state.searchList = searchList;
    },
    setSuggestList(state, suggestList) {
      state.suggestList = suggestList;
    }
  },

  actions: {
    reqSearch(context, name) {
      console.log(name);
      return axios
        .get("http://j4a102.p.ssafy.io:8000/search/" + name)
        .then(response => {
          console.log(response);
          if (response.data.message == "success") {
            context.commit("setIsSearched", true); // 검색된 상태로 변경
            context.commit("setSearchList", response.data.contents);
            return {
              result: true,
              msg: "검색 되었습니다",
              color: "success"
            };
          } else {
            return {
              result: false,
              msg: "검색에 실패하였습니다.",
              color: "warning"
            };
          }
        })
        .catch(error => {
          console.log(error);
          return {
            result: false,
            msg: "에러 발생",
            color: "warning"
          };
        });
    },
    reqSuggest(context, user_id) {
      return axios
        .get("http://j4a102.p.ssafy.io/recommendation/" + user_id)
        .then(response => {
          if (response.data.message == "success") {
            context.commit("setSuggestList", response.data.contents);

            return {
              result: true,
              msg: "추천 데이터 가져왔습니다.",
              color: "success"
            };
          } else {
            return {
              result: false,
              msg: "추천 데이터가 없습니다.",
              color: "warning"
            };
          }
        })
        .catch(error => {
          console.log(error);
          return {
            result: false,
            msg: "에러 발생",
            color: "warning"
          };
        });
    }
  }
};

export default FindStore;
