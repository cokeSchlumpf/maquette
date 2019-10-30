import _ from 'lodash';
import React from 'react';
import './styles.scss';

import { Link } from 'react-router-dom';

import Notification20 from '@carbon/icons-react/lib/notification/20'
import NotificationNew20 from '@carbon/icons-react/lib/notification--new/20'

import Add20 from '@carbon/icons-react/lib/add/20'

import {
    Content,
    Header,
    HeaderGlobalBar,
    HeaderGlobalAction,
    HeaderName,
    HeaderNavigation,
    HeaderMenuItem,
    HeaderPanel,
    Switcher,
    SwitcherItem,
    SwitcherItemLink,
} from 'carbon-components-react';

const emptyFunc = () => {};

export default ({
    children,
    brand = "",
    name = "",
    notifications = 0,
    userPanelExpanded = false,
    user = {},

    onClickNotifications = emptyFunc(),
    onClickUser = emptyFunc()
}) => {

    let userPanel = false;

    if (userPanelExpanded) {
        userPanel = (
            <HeaderPanel aria-label="Header Panel" expanded>
                <Switcher>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/profile" aria-label="User Profile">Signed in as { _.get(user, 'id', 'anonymous') }</SwitcherItemLink>
                    </SwitcherItem>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/profile" aria-label="User Profile">Your Profile</SwitcherItemLink>
                    </SwitcherItem>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/assets" aria-label="Your Assets">Your Assets</SwitcherItemLink>
                    </SwitcherItem>

                    <SwitcherItem>
                        <SwitcherItemLink href="/_auth/logout" aria-label="Logout">Logout</SwitcherItemLink>
                    </SwitcherItem>
                </Switcher>
            </HeaderPanel>)
    }

    return (
        <>
            <Header aria-label={ brand + " " + name }>
                <HeaderName href="#" prefix={ brand }>
                    { name }
                </HeaderName>

                <HeaderNavigation aria-label={ brand + " " + name }>
                    <HeaderMenuItem element={Link} to="/">Dashboard</HeaderMenuItem>
                    <HeaderMenuItem element={Link} to="/browse">Browse</HeaderMenuItem>
                </HeaderNavigation>

                <HeaderGlobalBar>
                    <HeaderNavigation aria-label="user menu">
                        <HeaderMenuItem element={Link} to="/user">{ _.get(user, 'name', 'anonymous') }</HeaderMenuItem>
                    </HeaderNavigation>

                    <HeaderGlobalAction
                        aria-label="Notifications"
                        onClick={ onClickNotifications }>

                        { notifications > 0 ? <NotificationNew20 /> : <Notification20 /> }
                    </HeaderGlobalAction>

                    <HeaderGlobalAction
                        aria-label="New Project">

                        <Add20 />
                    </HeaderGlobalAction>
                </HeaderGlobalBar>
            </Header>

            <Content>
                { children }
            </Content>

            { userPanel }
        </>
    );
};
