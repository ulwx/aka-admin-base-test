package org.github.ulwx.aka.admin.test.action.usermod;

import com.github.ulwx.aka.webmvc.annotation.AkaMvcActionMethod;
import com.github.ulwx.aka.webmvc.exception.ServiceException;
import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import com.github.ulwx.aka.webmvc.web.action.CbResultJson;
import com.ulwx.tool.RandomUtils;
import com.ulwx.tool.RequestUtils;
import com.ulwx.tool.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.github.ulwx.aka.admin.test.action.usermod.domain.MyUser;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Tag(name = "用户接口",description = "通用用户接口" )
public class UserAction extends ActionSupport {

    @Parameters({
            @Parameter(name="username",description="用户名称", required = true),
            @Parameter(name="userpass",description="用户密码", required = true),
            @Parameter(name="smscode",description="短信验证码", required = true)
    })
    @Operation(summary  = "登录",
            description  ="用于用户登录",
            requestBody = @RequestBody(
                    description = "Form data in the application/x-www-form-urlencoded format",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                            schema = @Schema(type = "object", implementation = RequestBodyBean.class)
                    )
            ),
            responses = {
                    @ApiResponse(description = "Successful Operation", responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json"
                                    , schemaProperties = {
                                            @SchemaProperty(name = "status",schema = @Schema(type="integer")),
                                            @SchemaProperty(name = "error",schema = @Schema(type="integer")),
                                            @SchemaProperty(name = "message",schema = @Schema(type="String")),
                                            @SchemaProperty(name = "data",schema = @Schema(type="object",
                                                    implementation=LoginRes.class
                                               ))
                                    }

                            )
                    )}
    )
    @AkaMvcActionMethod(httpMethod = "post")
    public String login(){
        this.getRequest().getSession().invalidate();
        RequestUtils ru=this.getRequestUtils();
        String userName=ru.getTrimString("username");
        String userPass=ru.getTrimString("userpass");
        String smsCode=ru.getTrimString("smscode");
        if(userName.isEmpty()){
            return this.JsonViewError("用户姓名不能为空！");
        }
        LoginRes loginRes=new LoginRes();
        loginRes.setMessage("登录成功！");
        String token=RandomUtils.getRandomNumberString(6);
        loginRes.setToken(token);
        HttpSession session=this.getRequest().getSession(true);
        session.setAttribute("token",token);
        return this.JsonViewSuc(loginRes);

    }
    public static class RequestBodyBean{
        private String charset;
        private Integer version;

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }
    @Schema(name = "LoginRes",description = "登录响应")
    public static class LoginRes{
        @Schema(name = "message",description = "消息")
        private String message;
        @Schema(name = "token",description = "token")
        private String token;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }


    @Operation(summary = "获取用户列表",description="用于获取用户列表，按条件查询")
    @RequestBody(
            description = " data in the json format",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object",implementation =UserQuery.class )

            ))
    @ApiResponse(description = "请求用户列表响应",content = @Content(schema = @Schema( anyOf = {CbResultJson.class,MyUser.class})))
    @AkaMvcActionMethod(httpMethod = "post", requestContentType ="application/json")
    public String getUserList(){

        String token=StringUtils.trim((String) this.getSession().getAttribute("token"));
        String requestToken= StringUtils.trim(this.getRequest().getHeader("Authorization"));
        if(!token.isEmpty()&&token.equals(requestToken)){
            ///
        }else{
            throw new ServiceException("token认证失败！");
        }
        RequestUtils ru=this.getRequestUtils();
        UserQuery query=ru.getBody(UserQuery.class);
        String userName=query.getUserName();
        String age=query.userAge;
        String userSex=query.userSex;
        List<MyUser> list=new ArrayList<>();
        MyUser user1=new MyUser();
        user1.setAge(10);
        user1.setBirth(LocalDate.of(2010,10,12));
        list.add(user1);

        MyUser user2=new MyUser();
        user2.setAge(10);
        user2.setBirth(LocalDate.of(2012,10,12));
        list.add(user2);

        return this.JsonViewSuc(list);

    }
    @Schema(description = "用户查询信息", name="UserQuer")
    public static class UserQuery{
        @Schema(description = "用户姓名")
        private String userName;
        @Schema(description = "用户年龄")
        private String userAge;
        @Schema(description = "用户性别")
        private String userSex;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserAge() {
            return userAge;
        }

        public void setUserAge(String userAge) {
            this.userAge = userAge;
        }

        public String getUserSex() {
            return userSex;
        }

        public void setUserSex(String userSex) {
            this.userSex = userSex;
        }
    }

}
