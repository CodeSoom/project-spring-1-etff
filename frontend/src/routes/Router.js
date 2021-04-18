import {Layout} from "antd";
import Main from "../components/meet/Main";
import Login from "../components/auth/Login";
import Join from "../components/auth/Join";
import {Redirect, Route, Switch} from "react-router-dom";
import {Content} from "antd/lib/layout/layout";
import Foot from "../components/Foot";
import MeetCreate from "../components/meet/MeetCreate";
import MeetDetail from "../components/meet/MeetDetail";
import Navigation from "../components/Navigation/Navigation";

const MyRouter = () => {
  return (
      <>
        <Navigation/>
        <Layout>
          <Layout className="site-layout-background">
            <Content style={{padding: "0 50px"}}>
              <Switch>
                <Route path="/" exact component={Main}/>
                <Route path="/login" exact component={Login}/>
                <Route path="/join" exact component={Join}/>
                <Route path="/meet/:id" exact component={MeetDetail}/>
                <Route path="/meet-create" exact component={MeetCreate}/>
                <Redirect from="*" to="/"/>
              </Switch>
            </Content>
            <Foot/>
          </Layout>
        </Layout>
      </>
  );
};

export default MyRouter;
