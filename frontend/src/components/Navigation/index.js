import React, {Component} from "react";
import LeftMenu from "./LeftMenu";
import RightMenu from "./RightMenu";
import {Button, Drawer} from "antd";

class Navbar extends Component {
  state = {
    current: "mail",
    visible: false,
  };
  showDrawer = () => {
    this.setState({
      visible: true,
    });
  };

  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  render() {
    return (
        <>
          <nav className="menuBar">
            <div className="menuCon">
              <div className="leftMenu">
                <LeftMenu/>
              </div>
              <div className="rightMenu">
                <RightMenu/>
              </div>
              <Button
                  className="barsMenu"
                  type="primary"
                  onClick={this.showDrawer}
              >
                <span className="barsBtn"></span>
              </Button>
              <Drawer
                  title="메뉴"
                  placement="right"
                  closable={false}
                  onClose={this.onClose}
                  visible={this.state.visible}
              >
                <LeftMenu/>
                <RightMenu/>
              </Drawer>
            </div>
          </nav>
        </>
    );
  }
}

export default Navbar;
