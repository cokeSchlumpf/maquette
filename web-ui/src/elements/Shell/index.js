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
    HeaderPanel,
    Switcher,
    SwitcherItem,
    SwitcherItemLink,
} from 'carbon-components-react';

export default function({
                            children,
                            notifications = 0,
                            userExpanded = false,

                            onClickNotifications = () => {},
                            onClickUser = () => {}}) {

    let userPanel = false;

    if (userExpanded) {
        userPanel = (
            <HeaderPanel aria-label="Header Panel" expanded>
                <Switcher>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/profile">You are logged in as Hippo</SwitcherItemLink>
                    </SwitcherItem>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/profile">Your Profile</SwitcherItemLink>
                    </SwitcherItem>
                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/assets">Your Assets</SwitcherItemLink>
                    </SwitcherItem>

                    <SwitcherItem>
                        <SwitcherItemLink element={Link} href="/_auth/logout">Logout</SwitcherItemLink>
                    </SwitcherItem>
                </Switcher>
            </HeaderPanel>)
    }

    return (
        <>
            <Header aria-label="Maquette Data Services">
                <HeaderName href="#" prefix="Maquette">
                    Data Services
                </HeaderName>

                <HeaderGlobalBar>

                    <HeaderGlobalAction
                        aria-label="Notifications"
                        onClick={ onClickNotifications }>

                        { notifications > 0 ? <NotificationNew20 /> : <Notification20 /> }
                    </HeaderGlobalAction>

                    <HeaderGlobalAction
                        aria-label="User"
                        isActive={ userExpanded }
                        onClick={ null }>
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
