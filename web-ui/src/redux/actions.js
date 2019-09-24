import app, { types as appTypes } from './app/actions';
import services, { types as servicesTypes } from './services/actions';
import views, { types as viewsTypes } from './views/actions';

export const types = {
    app: appTypes,
    services: servicesTypes,
    views: viewsTypes
};

export default {
    app,
    services,
    views
};