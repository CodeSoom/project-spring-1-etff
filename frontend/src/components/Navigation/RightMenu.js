import React, {useCallback} from "react";
import {Grid, Menu} from "antd";
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {LOGOUT_REQUEST} from "../../redux/types";

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

const {useBreakpoint} = Grid;

const RightMenu = () => {
    const {md} = useBreakpoint();
    const dispatch = useDispatch();
    const {isAuthenticated, memberName} = useSelector((state) => state.auth);

    const onLogout = useCallback(() => {
        dispatch({
            type: LOGOUT_REQUEST,
        });
    }, [dispatch]);

    return (
        <Menu mode={md ? "horizontal" : "inline"}>
            <Menu.Item key="login">
                {!isAuthenticated ? (
                    <Link to="/login">LOGIN</Link>
                ) : (
                    <Link onClick={onLogout} to="#">
                        LOGOUT
                    </Link>
                )}
            </Menu.Item>

            <Menu.Item key="sign">
                {!isAuthenticated ? (
                    <Link to="/join">JOIN</Link>
                ) : (
                    <Link onClick={onLogout} to="#">
                        My Info
                    </Link>
                )}
            </Menu.Item>
        </Menu>
    );
};

export default RightMenu;
