import { createStore, combineReducers, applyMiddleware } from 'redux';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import { thunk } from 'redux-thunk';
import authReducer from './reducers/authReducer';
import sidebarReducer from './reducers/sidebarReducers';
import filterReducer from "./reducers/filterReducer";

const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['auth'],
};

const rootReducer = combineReducers({
  auth: authReducer,
  sidebar: sidebarReducer,
  filters: filterReducer,
});

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = createStore(persistedReducer, applyMiddleware(thunk));  //, applyMiddleware(thunk)
export const persistor = persistStore(store);
