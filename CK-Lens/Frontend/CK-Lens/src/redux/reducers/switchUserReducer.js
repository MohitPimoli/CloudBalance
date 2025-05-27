const initialState = {
    isSwitched: false,
};

const switchUserReducer = (state = initialState, action) => {
    switch (action.type) {
        case "SET_SWITCH_USER":
            return {
                ...state,
                isSwitched: !state.isSwitched,
            };
        default:
            return state;
    }
}
export default switchUserReducer;