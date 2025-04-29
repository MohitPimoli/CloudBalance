import { SET_FILTERS, CLEAR_FILTERS, UPDATE_FIELD_FILTERS } from "../actions/filterActions";

const initialState = {};

const filtersReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_FILTERS:
            return { ...action.payload };

        case CLEAR_FILTERS:
            return {};

        case UPDATE_FIELD_FILTERS: {
            const { fieldName, values } = action.payload;
            if (!values || values.length === 0) {
                const { [fieldName]: _, ...rest } = state;
                return rest;
            }
            return {
                ...state,
                [fieldName]: values,
            };
        }

        default:
            return state;
    }
};

export default filtersReducer;
