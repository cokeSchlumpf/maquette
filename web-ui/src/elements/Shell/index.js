import _ from 'lodash';
import React from 'react';
import './styles.scss';

import { Link } from 'react-router-dom';

import Notification20 from '@carbon/icons-react/lib/notification/20'
import NotificationNew20 from '@carbon/icons-react/lib/notification--new/20'
import User20 from '@carbon/icons-react/lib/user/20'

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

export default ({
                            children,
                            notifications = 0,
                            userPanelExpanded = false,
                            user = {},

                            onClickNotifications = () => {},
                            onClickUser = () => {}}) => {

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
            <Header aria-label="Peppermint Insurance Data Services">
                <HeaderName href="#" prefix="Peppermint">
                    Data Services
                </HeaderName>

                <HeaderNavigation aria-label="IBM [Platform]">
                    <HeaderMenuItem element={Link} to="/">Dashboard</HeaderMenuItem>
                    <HeaderMenuItem element={Link} to="/assets">Assets</HeaderMenuItem>
                </HeaderNavigation>

                <HeaderGlobalBar>

                    <HeaderGlobalAction
                        aria-label="Notifications"
                        onClick={ onClickNotifications }>

                        { notifications > 0 ? <NotificationNew20 /> : <Notification20 /> }
                    </HeaderGlobalAction>

                    <HeaderGlobalAction
                        aria-label="User"
                        isActive={ userPanelExpanded }
                        onClick={ onClickUser }>
                        <User20 />
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
