import {
  CLEAR_ERROR_FAILURE,
  CLEAR_ERROR_REQUEST,
  CLEAR_ERROR_SUCCESS,
  MEET_DETAIL_LOADING_FAILURE,
  MEET_DETAIL_LOADING_REQUEST,
  MEET_DETAIL_LOADING_SUCCESS,
  MEET_JOIN_FAILURE,
  MEET_JOIN_REQUEST,
  MEET_JOIN_SUCCESS,
  MEET_LOADING_FAILURE,
  MEET_LOADING_REQUEST,
  MEET_LOADING_SUCCESS,
} from "../types";

const initialState = {
  isAuthenticated: null,
  meets: [],
  meetDetail: null,
  meetCount: "",
  loading: false,
  error: "",
  title: "",
  searchBy: "",
  searchResult: "",
  previousMatchMsg: "",
};

export default function (state = initialState, action) {
  switch (action.type) {
    case MEET_LOADING_REQUEST:
      return {
        ...state,
        meets: [],
        loading: true,
      };
    case MEET_LOADING_SUCCESS:
      return {
        ...state,
        meets: [...state.meets, ...action.payload.content],
        meetCount: action.payload.totalElements,
        loading: false,
      };

    case MEET_LOADING_FAILURE:
      return {
        ...state,
        loading: false,
      };

    case MEET_DETAIL_LOADING_REQUEST:
      return {
        ...state,
        meets: [],
        loading: true,
      };
    case MEET_DETAIL_LOADING_SUCCESS:
      return {
        ...state,
        loading: false,
        meetDetail: action.payload,
        title: action.payload.title,
      };

    case MEET_DETAIL_LOADING_FAILURE:
      return {
        ...state,
        error: action.payload,
        loading: false,
      };

    case MEET_JOIN_REQUEST:
      return {
        ...state,
        meetDetail: null,
        loading: true,
      };
    case MEET_JOIN_SUCCESS:
      return {
        ...state,
        loading: false,
        meetDetail: action.payload,
        title: action.payload.title,
      };

    case MEET_JOIN_FAILURE:
      return {
        ...state,
        error: action.payload,
        loading: false,
      };

    case CLEAR_ERROR_REQUEST:
      return {
        ...state,
        errorMsg: "",
      };
    case CLEAR_ERROR_SUCCESS:
      return {
        ...state,
        errorMsg: null,
      };
    case CLEAR_ERROR_FAILURE:
      return {
        ...state,
        errorMsg: null,
      };

    default:
      return state;
  }
}
