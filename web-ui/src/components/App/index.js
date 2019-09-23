import 'carbon-components/scss/globals/scss/styles.scss';

import React, { Component } from 'react';
import './styles.css';

import { Notification20, User20 } from '@carbon/icons-react';

import {
    Content,
    Header,
    HeaderGlobalBar,
    HeaderGlobalAction,
    HeaderName
} from 'carbon-components-react';

class App extends Component {

  render() {
    return (
        <div className="App">
          <Header aria-label="Maquette Data Services">
            <HeaderName href="#" prefix="Maquette">
              Data Services
            </HeaderName>

            <HeaderGlobalBar>

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
            { this.props.children }
          </Content>
        </div>
    );
  }

}

export default App;
