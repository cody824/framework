(function ($, window) {

    var __userAction = {};

    var BASE_SECURITL_URL = "/security";
    /**
     * 注册用户
     * @param type  email/mobile/nick
     * @param user  用户对象
     * @param authcode 验证码
     * @param cb    回调函数
     */
    __userAction.register = function (type, user, authcode, cb) {
        if (!user.params) {
            var params = {};
            $.each(user, function (key, value) {
                if (key === "nick" || key === "password"
                    || key === "email" || key === "mobile") {
                    return true;
                }
                delete user[key];
                params[key] = value;
            });
            user.params = params;
        }
        SureAjax.ajax({
            url: BASE_SECURITL_URL + "/auth/register/" + type + "?authcode=" + authcode,
            type: 'post',
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json"
            },
            dataType: "json",
            data: JSON.stringify(user),
            success: cb
        })
    };

    /**
     * 发送验证码
     * @param type email/mobile
     * @param to   邮箱或者手机
     * @param cb   回调函数
     */
    __userAction.sendVerificationCode = function (type, to, cb) {
        SureAjax.ajax({
            url: BASE_SECURITL_URL + "/verification-code/" + type,
            type: 'post',
            headers: {
                "Accept": 'application/json'
            },
            data: {
                to: to
            },
            success: cb
        });
    };

    /**
     * 修改密码
     * @param userId    用户ID
     * @param oldPassword   旧密码
     * @param newPassword   新密码
     * @param cb    回调函数
     */
    __userAction.resetPassword = function (userId, oldPassword, newPassword, cb) {
        SureAjax.ajax({
            url: BASE_SECURITL_URL + "/user/" + userId + "/password",
            type: 'put',
            headers: {
                "Accept": 'application/json'
            },
            data: {
                oldPassword: oldPassword,
                newPassword: newPassword
            },
            success: cb
        });
    }

    /**
     * 通过邮箱重置密码
     * @param email
     * @param authcode
     * @param newPassword
     * @param cb
     */
    __userAction.resetPasswordByEmail = function (email, authcode, newPassword, cb) {
        SureAjax.ajax({
            url: BASE_SECURITL_URL + "/auth/resetpsd/email",
            type: 'post',
            headers: {
                "Accept": 'application/json'
            },
            data: {
                email: email,
                authcode: authcode,
                password: newPassword
            },
            success: cb
        });
    }

    /**
     * 更新用户详情
     * @param userId    用户ID
     * @param userDetail    用户详情
     * @param cb    回调函数
     */
    __userAction.updateUD = function (userId, userDetail, cb) {
        SureAjax.ajax({
            url: BASE_SECURITL_URL + "/ud/" + userId,
            type: 'put',
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json"
            },
            dataType: "json",
            data: JSON.stringify(userDetail),
            success: cb
        })
    }

    window.UA = __userAction;

}($, window));