import {useCallback} from "react";
import {Header} from "antd/lib/layout/layout";
import {HomeTwoTone, UserOutlined} from "@ant-design/icons";
import {useDispatch, useSelector} from "react-redux";
import {Avatar, Button, Col, Row, Space} from "antd";
import {Link} from "react-router-dom";
import {LOGOUT_REQUEST} from "../redux/types";

const NavBar = () => {
  const dispatch = useDispatch();
  const {isAuthenticated} = useSelector((state) => state.auth);

  const onLogout = useCallback(() => {
    dispatch({
      type: LOGOUT_REQUEST,
    });
  }, [dispatch]);

  return (
      <Header>
        <Row>
          <Col span={1}/>
          <Col span={19}>
            <div className="icons-list">
              <Link to="/">
                <HomeTwoTone style={{fontSize: "20px"}}/>
              </Link>
            </div>
          </Col>
          <Col span={4}>
            <Space size="middle">
              {!isAuthenticated ? (
                  <Link to="/login">
                    <Button type="dashed">LOGIN</Button>
                  </Link>
              ) : (
                  <Link onClick={onLogout} to="#">
                    <Button type="dashed">LOGOUT</Button>
                  </Link>
              )}
              {!isAuthenticated ? (
                  <Link to="/join">
                    <Button type="dashed">JOIN</Button>
                  </Link>
              ) : (
                  <Link onClick={onLogout} to="#">
                    <Avatar
                        style={{
                          backgroundColor: "#87d068",
                        }}
                        icon={<UserOutlined/>}
                    />
                  </Link>
              )}
            </Space>
          </Col>
        </Row>
      </Header>
  );
};

export default NavBar;
