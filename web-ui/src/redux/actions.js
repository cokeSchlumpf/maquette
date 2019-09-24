import app, { types as appTypes } from './app/actions';
import components, { types as componentsTypes } from './components/actions';
import services, { types as servicesTypes } from './services/actions';
import views, { types as viewsTypes } from './views/actions';

export const types = {
    app: appTypes,
    components: componentsTypes,
    services: servicesTypes,
    views: viewsTypes
};

export default {
    app,
    components,
    services,
    views
};