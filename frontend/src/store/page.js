const state = {
    totalAlarmCnt: '',
    curPage: 0,
    offset: 0,
    isDel: false,
  };
  
  const getters = {
    totalAlarmCnt(state) {
      return state.totalAlarmCnt;
    },
    curPage(state) {
      return state.curPage;
    },
    isDel(state) {
      return state.isDel;
    },
    offset(state) {
      return state.offset;
    },
    // get_id: (state) => state.id,
  };
  
  const mutations = {
    SET_TOTALALARMCNT(state, payload) {
      state.totalAlarmCnt = payload;
    },
    SET_CURPAGE(state, payload) {
      state.curPage = payload;
    },
    SET_OFFSET(state, payload) {
      state.offset = payload;
    },
    TOGGLE_ISDEL(state, payload) {
      state.isDel = payload;
    },
  };
  
  const actions = {
    async set_totalAlarmCnt(context, data) {
      await context.commit('SET_TOTALALARMCNT', data);
    },
    async set_curPage(context, data) {
      await context.commit('SET_CURPAGE', data);
    },
    async toggle_isDel(context, data) {
      await context.commit('TOGGLE_ISDEL', data);
    },
    async set_offset(context, data) {
      await context.commit('SET_OFFSET', data);
    },
  };
  
  export default {
    strict: process.env.NODE_ENV !== 'production',
    state: {
      ...state,
    },
    getters,
    mutations,
    actions,
  };
  