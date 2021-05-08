package com.llw.speechsynthesis.model;

/**
 * 获取鉴权认证Token响应实体
 *
 * @author llw
 * @date 2021/5/7 16:16
 */
public class GetTokenResponse {


    /**
     * refresh_token : 25.0141c302b0f460cd0500827fa31f22ce.315360000.1935736936.282335-24113250
     * expires_in : 2592000
     * session_key : 9mzdCS6a/7/wIFWLR8zFoYs2koSri++RGhSecVXM/vY93At4kxYRajL/xMV17MoxcTAJfadRVaSBxokIqFeQoxsZ8e3NPQ==
     * access_token : 24.2830c05696b214cf07bfbdf764599b39.2592000.1622968936.282335-24113250
     * scope : audio_voice_assistant_get brain_enhanced_asr audio_tts_post brain_speech_realtime public brain_all_scope picchain_test_picchain_api_scope brain_asr_async wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test权限 vis-classify_flower lpq_开放 cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base smartapp_mapp_dev_manage iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi fake_face_detect_开放Scope vis-ocr_虚拟人物助理 idl-video_虚拟人物助理 smartapp_component smartapp_search_plugin avatar_video_test
     * session_secret : 2cdde5fd8f3fd4394c1b090e2ffa2d1c
     */

    private String refresh_token;
    private int expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSession_secret() {
        return session_secret;
    }

    public void setSession_secret(String session_secret) {
        this.session_secret = session_secret;
    }
}
