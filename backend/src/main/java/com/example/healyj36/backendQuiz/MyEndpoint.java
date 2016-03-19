/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

/*
INFO FROM GOOGLE APP ENGINE
project: QuizApp

project ID: quizapp-1243

Project number: 522187312304
*/

package com.example.healyj36.backendQuiz;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        clientIds = {"522187312304-is53c3gsmeupjf452u59c1hprt3qli0e.apps.googleusercontent.com"},
        audiences = {"522187312304-7q8l624l50skst93m9ogtqldndcu5g37.apps.googleusercontent.com"},
        /*
        scopes = {Constants.EMAIL_SCOPE}, //*
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        */
        namespace = @ApiNamespace(
                ownerDomain = "backendQuiz.healyj36.example.com",
                ownerName = "backendQuiz.healyj36.example.com",
                packagePath = ""
        )
)
/*
class Constants {
    public static final String WEB_CLIENT_ID = "522187312304-7q8l624l50skst93m9ogtqldndcu5g37.apps.googleusercontent.com";
    public static final String ANDROID_CLIENT_ID = "522187312304-is53c3gsmeupjf452u59c1hprt3qli0e.apps.googleusercontent.com";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;

    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"; //*
}
*/
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(User user, @Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

/*

     * Provides the ability to insert a new Score entity.

    @ApiMethod(name = "scores.insert")
    public MyBean insert(int score, User user) throws OAuthRequestException, IOException {
        MyBean response = new MyBean();
        response.setData(user + "'s score is " + score);
        return response;
    }
    */
}
