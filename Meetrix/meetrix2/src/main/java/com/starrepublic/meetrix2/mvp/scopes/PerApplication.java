package com.starrepublic.meetrix2.mvp.scopes;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;


@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApplication {
}
