import React, {Component} from 'react'
import Shell from '../../elements/Shell'

class App extends Component {

  render() {
    return <Shell
        notifications={ 2 }
        userExpanded={ false }

        onClickNotifications={ () => console.log('notifications clicked') }>{this.props.children}</Shell>;
  }

}

export default App;
