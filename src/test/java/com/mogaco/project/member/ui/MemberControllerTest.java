package com.mogaco.project.member.ui;

import com.mogaco.project.auth.application.AuthenticationGuard;
import com.mogaco.project.auth.application.AuthenticationService;
import com.mogaco.project.common.BaseControllerTest;
import com.mogaco.project.member.application.MemberNotFoundException;
import com.mogaco.project.member.application.MemberService;
import com.mogaco.project.member.dto.MemberRegisterDto;
import com.mogaco.project.member.dto.MemberResponse;
import com.mogaco.project.member.dto.MemberUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberControllerTest extends BaseControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY0MDk2MjgwMCwiZXh" +
            "wIjoxNjQwOTYzMTAwfQ.2siRnBJmRU2JXjZY0CkQMgnCHRJN4Dld4_wG6R7T-HQ";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private static Long GIVEN_ID = 1L;
    private static Long NOT_EXISTED_ID = -1L;
    private static String GIVEN_NAME = "user1";
    private static String GIVEN_EMAIL = "test@test.com";
    private static String GIVEN_PASSWORD = "test";
    private static String UPDATE_NAME = GIVEN_NAME + "_UPDATED";
    private static String UPDATE_EMAIL = GIVEN_EMAIL + "_UPDATED";

    @MockBean
    private MemberService memberService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean(name = "authenticationGuard")
    private AuthenticationGuard authenticationGuard;

    private MemberRegisterDto registerDto;

    private MemberUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        registerDto =
                MemberRegisterDto.builder()
                        .name(GIVEN_NAME)
                        .email(GIVEN_EMAIL)
                        .password(GIVEN_PASSWORD)
                        .build();

        updateDto = new MemberUpdateDto(UPDATE_NAME, UPDATE_EMAIL);

        given(memberService.registerMember(any(MemberRegisterDto.class))).willReturn(GIVEN_ID);
    }

    @DisplayName("????????? ?????? ?????? ???????????? ????????????")
    @Test
    void registerMemberWithValidAttributes() throws Exception {
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.
                                post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(content().string("1"))
                .andDo(print())
                .andDo(document("register-member",
                        requestFields(
                                attributes(key("member").value("Fields for member creation")),
                                fieldWithPath("email").type(STRING).description("????????? ?????????")
                                        .attributes(key("constraints").value("?????? ??? ?????? ?????? ?????????????????????.")),
                                fieldWithPath("name").type(STRING).description("????????? ??????")
                                        .attributes(key("constraints").value("??? ?????? ?????? ?????????????????????.")),
                                fieldWithPath("password").type(STRING).description("????????? ????????????")
                                        .attributes(key("constraints").value("??????????????? 4 ~ 32 ?????? ?????? ?????????????????????."))
                        ))
                );
        verify(memberService).registerMember(any(MemberRegisterDto.class));
    }

    @DisplayName("???????????? ?????? ?????? ?????? ???????????? ????????????")
    @Test
    void createWithInvalidAttributes() throws Exception {
        MemberRegisterDto dto = MemberRegisterDto.builder().build();
        mockMvc
                .perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .header("Authorization", "Bearer " + VALID_TOKEN)
                )

                .andExpect(status().isBadRequest());
    }

    @DisplayName("????????? ?????? ?????? ?????? ???????????? ?????? ??????")
    @Test
    void updateUserWithValidAttributes() throws Exception {
        given(authenticationGuard.checkIdMatch(any(Authentication.class), anyLong()))
                .willReturn(true);

        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.
                                patch("/api/v1/members/{id}", GIVEN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                                .header("Authorization", "Bearer " + VALID_TOKEN)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-member",
                        requestHeaders(headerWithName("Authorization").description("JWT ??????")),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        requestFields(
                                attributes(key("member").value("Fields for member creation")),
                                fieldWithPath("email").type(STRING).description("?????? ?????????"),
                                fieldWithPath("name").type(STRING).description("?????? ??????")
                                        .attributes(key("constraints").value("??? ?????? ?????? ?????????????????????."))
                        ))
                );

    }

    @DisplayName("???????????? ?????? ?????? ?????? ?????? ???????????? ?????? ??????")
    @Test
    void updateUserWithInValidAttributes() throws Exception {
        given(authenticationGuard.checkIdMatch(any(Authentication.class), anyLong()))
                .willReturn(true);

        MemberRegisterDto dto = new MemberRegisterDto();
        mockMvc
                .perform(
                        patch("/api/v1/members/" + GIVEN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .header("Authorization", "Bearer " + VALID_TOKEN)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("???????????? ?????? ?????? ????????????")
    @Test
    void detailWithLoginMember() throws Exception {
        given(memberService.getMember(GIVEN_ID))
                .willReturn(
                        MemberResponse.builder()
                                .id(GIVEN_ID)
                                .email(GIVEN_EMAIL)
                                .name(GIVEN_NAME)
                                .build()
                );
        given(authenticationService.parseToken(VALID_TOKEN))
                .willReturn(GIVEN_ID);

        mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .get("/api/v1/members/me")
                                .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GIVEN_NAME)))
                .andExpect(content().string(containsString(GIVEN_EMAIL)))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(content().string(containsString(GIVEN_NAME)))
                .andDo(document("myinfo",
                        requestHeaders(headerWithName("Authorization").description("JWT ??????")),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("?????? ?????????"),
                                fieldWithPath("name").type(STRING).description("?????? ??????"),
                                fieldWithPath("email").type(STRING).description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("????????? ?????? ?????? ????????????")
    @Test
    void detailWithExistedMember() throws Exception {
        given(memberService.getMember(GIVEN_ID))
                .willReturn(
                        MemberResponse.builder().id(GIVEN_ID).email(GIVEN_EMAIL).name(GIVEN_NAME).build());
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .get("/api/v1/members/{id}", 1L)
                                .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GIVEN_NAME)))
                .andExpect(content().string(containsString(GIVEN_EMAIL)))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(content().string(containsString(GIVEN_NAME)))
                .andDo(document("get-member",
                        requestHeaders(headerWithName("Authorization").description("JWT ??????")),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("?????? ?????????"),
                                fieldWithPath("name").type(STRING).description("?????? ??????"),
                                fieldWithPath("email").type(STRING).description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("???????????? ?????? ?????? ?????? ????????????")
    @Test
    void detailWithNotExistedMember() throws Exception {
        given(memberService.getMember(NOT_EXISTED_ID)).willThrow(MemberNotFoundException.class);

        mockMvc.perform(get("/api/v1/members/{id}", NOT_EXISTED_ID)
                .header("Authorization", "Bearer " + VALID_TOKEN)

        ).andExpect(status().isNotFound());
    }

    @DisplayName("????????? ?????? ????????? ????????????")
    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .delete("/api/v1/members/{id}", GIVEN_ID)
                        .header("Authorization", "Bearer " + VALID_TOKEN)

        ).andExpect(status().isNoContent())
                .andDo(document("delete-member",
                        pathParameters(
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        requestHeaders(headerWithName("Authorization").description("JWT ??????"))
                ));

        verify(memberService).deleteMember(GIVEN_ID);
    }

    @DisplayName("???????????? ?????? ?????? ????????? ????????????")
    @Test
    void destroyWithNotExistedId() throws Exception {
        doThrow(new MemberNotFoundException(NOT_EXISTED_ID))
                .when(memberService)
                .deleteMember(NOT_EXISTED_ID);

        mockMvc
                .perform(delete("/api/v1/members/{id}", NOT_EXISTED_ID)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                )
                .andExpect(status().isNotFound());
    }
}
