import 'carbon-components/scss/globals/scss/styles.scss';

import React from 'react';
import logo from '../../logo.svg';
import './styles.css';

import { Search20, Notification20, User20 } from '@carbon/icons-react';

import {
  Content,
  Header,
  HeaderGlobalBar,
  HeaderGlobalAction,
  HeaderName
} from 'carbon-components-react';

function App() {
  return (
    <div className="App">
      <Header aria-label="IBM Platform Name">
        <HeaderName href="#" prefix="Maquette">
          Data Platform
        </HeaderName>

        <HeaderGlobalBar>
          <HeaderGlobalAction
              aria-label="Search"
              onClick={ null }>
            <Search20 />
          </HeaderGlobalAction>

          <HeaderGlobalAction
              aria-label="Notifications"
              onClick={ null }>
            <Notification20 />
          </HeaderGlobalAction>

          <HeaderGlobalAction
              aria-label="User"
              onClick={ null }>
            <User20 />
          </HeaderGlobalAction>
        </HeaderGlobalBar>
      </Header>

      <Content>
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload. Helloooo!
        </p>
        <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
        >
          Learn React</a> - <a
            className="App-link"
            href="https://www.carbondesignsystem.com/"
            target="_blank"
            rel="noopener noreferrer"
        >
          Learn Carbon Design System
        </a>
      </Content>
    </div>
  );
}

export default App;
