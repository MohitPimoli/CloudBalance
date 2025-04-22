// src/store/sidebar/sidebarReducer.js
import { TOGGLE_SIDEBAR, SET_SIDEBAR_OPEN } from "../actions/sidebarAction";

const initialState = {
    open: true,
};

const sidebarReducer = (state = initialState, action) => {
    switch (action.type) {
        case TOGGLE_SIDEBAR:
            return { ...state, open: !state.open };

        case SET_SIDEBAR_OPEN:
            return { ...state, open: action.payload };

        default:
            return state;
    }
};

export default sidebarReducer;
