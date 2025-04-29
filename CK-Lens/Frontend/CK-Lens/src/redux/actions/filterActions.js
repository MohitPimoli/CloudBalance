export const SET_FILTERS = "filters/SET_FILTERS";
export const CLEAR_FILTERS = "filters/CLEAR_FILTERS";
export const UPDATE_FIELD_FILTERS = "filters/UPDATE_FIELD_FILTERS";

export const setFilters = (filters) => ({
    type: SET_FILTERS,
    payload: filters,
});

export const clearFilters = () => ({
    type: CLEAR_FILTERS,
});

export const updateFieldFilters = (fieldName, values) => ({
    type: UPDATE_FIELD_FILTERS,
    payload: { fieldName, values },
});