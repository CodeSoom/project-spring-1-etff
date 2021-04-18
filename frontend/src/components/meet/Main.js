import {Breadcrumb, Button, Card, Layout, PageHeader, Row} from "antd";
import {useEffect} from "react";
import {Helmet} from "react-helmet";
import "antd/dist/antd.css";
import {useDispatch, useSelector} from "react-redux";
import {MEET_LOADING_REQUEST} from "../../redux/types";
import {GrowingSpinner} from "../spinner/Spinner";
import MeetCardOne from "../meet/MeetCardOne";
import {Link} from "react-router-dom";

const {Content} = Layout;
const {Meta} = Card;

const Main = () => {
    const {meets} = useSelector((state) => state.meet);
    const dispatch = useDispatch();

    const {isAuthenticated} = useSelector((state) => state.auth);

    useEffect(() => {
        dispatch({type: MEET_LOADING_REQUEST});
    }, [dispatch]);

    return (
        <>
            <Helmet title="Home"/>
            <PageHeader
                title="이번주 모각코"
                className="site-page-header"
                style={{margin: 50}}
                extra={[
                    <Button key="1" type="primary">
                        {isAuthenticated ? (
                            <Link to="/meet-create">모임 만들기</Link>
                        ) : (
                            <Link to="/login">모임 만들기</Link>
                        )}
                    </Button>,
                ]}
            ></PageHeader>
            <Content
                className="site-layout"
                style={{padding: "0 50px", marginTop: 30}}
            >
                <Breadcrumb style={{margin: "16px 0"}}>
                    <Breadcrumb.Item>
                        <h2>이번주 모각코</h2>
                    </Breadcrumb.Item>
                </Breadcrumb>
                <div
                    className="site-layout-background"
                    style={{padding: 24, minHeight: 380}}
                >
                    <Row gutter={16}>
                        {meets ? <MeetCardOne meets={meets}/> : GrowingSpinner}
                    </Row>
                </div>
            </Content>
        </>
    );
};

export default Main;
