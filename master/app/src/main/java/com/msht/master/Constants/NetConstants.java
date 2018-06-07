package com.msht.master.Constants;


/**
 * url的静态字段
 * Created by hei123 on 11/14/2016.
 */
public class NetConstants {
    /**
     * 服务器地址 http://msbapp.cn/
     * https://test.msbapp.cn/
     */
   // private static final String SERVER_URL="https://msbapp.cn/";
   // private static final String SERVER_URL="https://test.msbapp.cn/";
    private static final String SERVER_URL="http://test.msbapp.cn:8080/";

    /**
     * 师傅用户登陆
     */
    public static final String REPAIRMAN_LOGIN=SERVER_URL+"repairman/repairman/login";
    /**
     * 师傅注册
     */
    public static final String REPAIRMAIN_REGIST=SERVER_URL+"repairman/repairman/regist";
    /**
     * 师傅重置密码
     */
    public static final String REPAIRMAN_RESET_PASSWD=SERVER_URL+"repairman/repairman/reset_passwd";
    /**
     * 获取验证码
     */
    public static final String REPAIRMAN_CAPTCHA=SERVER_URL+"Gas/user/captcha";
    /**
     * 获取师傅的工作状态
     */
    public static final String REPAIRMAN_STATUS=SERVER_URL+"repairman/repairman/status";
    /**
     * 获取订单列表
     */
    public static final String REPAIR_ORDER_LIST=SERVER_URL+"repairman/repair_order/list";
    /**
     * 搜索工单
     */
    public static final String SEARCH_ORDER_LIST=SERVER_URL+"repairman/repair_order/search";
    /**
     * 确定订单
     */
    public static final String REPAIR_ORDER_ACCEPT=SERVER_URL+"repairman/repair_order/accept";
    /**
     * 更改师傅工作状态
     */
    public static final String CHANGE_WORK_STATUS=SERVER_URL+"repairman/repairman/change_work_status";

    /**
     * 意见反馈
     */
    public static final String FEEDBACK=SERVER_URL+"repairman/feedback/add";

    /**
     * 新增银行卡
     */
    public static final String BANKCARD_ADD=SERVER_URL+"repairman/bankcard/add";


    /**
     * 所有维修类目
     */
    public static final String REPAIR_CATEGORY_ALL=SERVER_URL+"repairman/repair_category/all";
    /**
     * 师傅提交认证
     */
    public static final String REPAIRMAN_APPLY_VALID=SERVER_URL+"repairman/repairman/apply_valid";

    /**
     * 获取城市区列表
     */
    public static final String CITY_DISTRICT=SERVER_URL+"repairman/city/district";

    /**
     * 师傅新增证书
     */
    public static final String ADD_CERTIFICATE=SERVER_URL+"repairman/certificate/add";
    /**
     * 银行卡列表
     */
    public static final String BANKCARD_LIST=SERVER_URL+"repairman/bankcard/list";
    /**
     * 删除银行卡
     */
    public static final String BANKCARD_DELETE=SERVER_URL+"repairman/bankcard/delete";

    /**
     * 消息列表
     */
    public static final String MESSAGE_LIST=SERVER_URL+"repairman/message/list";

    /**
     * 我的资料
     */
    public static final String REPAIRMAN_DETAIL=SERVER_URL+"repairman/repairman/detail";

    /**
     * 我的钱包 总览
     */
    public static final String WALLET_OVERVIEW=SERVER_URL+"repairman/wallet/overview";
    public static final String EnterPrise_Overview=SERVER_URL+"repairman/wallet/ep_overview";
    /**
     * 创建订单
     */
    public static final String CREATE_ORDER=SERVER_URL+"Gas/payment/createOrder";

    /**
     * 维修类目 全部分类 包含师傅选择
     */
    public static final String REPAIR_CATEGORY_REPAIRMAN_ALL=SERVER_URL+"repairman/repair_category/repairman_all";

    /**
     * 修改维修项目
     */
    public static final String MODIFY_CATEGORY=SERVER_URL+"repairman/repairman/modify_category";
    /**
     * 可提现金额
     */
    public static final String CAN_WITHDRAWALS=SERVER_URL+"repairman/wallet/can_withdrawals";

    /**
     * 提交提现申请
     */
    public static final String WITHDRAWALS=SERVER_URL+"repairman/wallet/withdrawals";
    /**
     * 工单详情
     */
    public static final String REPAIR_ORDER_VIEW=SERVER_URL+"repairman/repair_order/view";

    /**
     * 生成账单
     */
    public static final String REPAIR_ORDER_BILL=SERVER_URL+"repairman/repair_order/bill";
    /**
     * 转单
     */
    public static final String REPAIR_ORDER_CANCAL=SERVER_URL+"repairman/repair_order/cancel";
    /**
     * 我的评价列表
     */
    public static final String ORDER_EVALUATE_LIST=SERVER_URL+"repairman/order_evaluate/list";
    /**
     * 师傅基本信息
     */
    public static final String BASCI_INFO=SERVER_URL+"repairman/repairman/basic_info";

    /**
     * 工单统计
     */
    public static final String ORDER_COUNT=SERVER_URL+"repairman/repair_order/order_count";

    /**
     * 师傅月收入统计
     */
    public static final String MONTH_INCOME=SERVER_URL+"repairman/wallet/month_income";

    /**
     * 检测新版本
     */
    public static final String NEW_VERSION=SERVER_URL+"repairman/app/version";
    /**
     * 提现历史
     */
    public static final String WITHDRAWALS_LIST=SERVER_URL+"repairman/wallet/withdrawals_list";
    /**
     * 奖励历史
     */
    public static final String REWARD_HISTORY=SERVER_URL+"repairman/wallet/reward_list";
    /**
     * 质保金变动历史
     */
    public static final String QUALITY_ASSURANCE_LIST=SERVER_URL+"repairman/wallet/quality_assurance_list";

    /**
     * 公告列表
     */
    public static final String ANNOUNCE_LIST=SERVER_URL+"repairman/announce/list";

    public static final String CERTIFICATELIST=SERVER_URL+"repairman/certificate/list";



    /**
     * 下面时H5的页面
     */

    /**
     * 用户注册协议
     */
    public static final String REGIST_AGREEMENT=SERVER_URL+"repairman_h5/regist_agreement.html";

    /**
     * 佣金规则
     */
    public static final String COMMISSION_RULE=SERVER_URL+"repairman_h5/commission_rule.html";
    /**
     * 惩罚规则
     */
    public static final String PUNISH_RULE=SERVER_URL+"repairman_h5/punish_rule.html";
    /**
     * 质保金变动历史
     */
    public static final String QUALITY_ASSURANCE_LIST_H5=SERVER_URL+"repairman/h5/quality_assurance_list";
    /**
     * 奖励历史
     */
    public static final String REWARK_LIST_H5=SERVER_URL+"repairman/h5/reward_list";

    /**
     * 月收入统计
     */
    public static final String MONTH_INCOME_H5 = SERVER_URL + "repairman/h5/month_income";


    /**
     * 维修价格-卫浴-水管
     */
    public static final String SHUI_GUAN=SERVER_URL+"repair_h5/weiyu_shuiguan.html";
    /**
     * 维修价格-卫浴-水龙头
     */
    public static final String SHUI_LONG_TOU=SERVER_URL+"repair_h5/weiyu_shuilongtou.html";
    /**
     * 维修价格-卫浴-花洒
     */
    public static final String HUASA=SERVER_URL+"repair_h5/weiyu_huasa.html";
    /**
     * 维修价格-卫浴-马桶
     */
    public static final String MATONG=SERVER_URL+"repair_h5/weiyu_matong.html";
    /**
     * 维修价格-卫浴-浴室柜
     */
    public static final String YUSHIGUI=SERVER_URL+"repair_h5/weiyu_yushigui.html";
    /**
     * 维修价格-家电-燃气灶
     */
    public static final String RANQIZAO=SERVER_URL+"repair_h5/jiadian_ranqizao.html";
    /**
     * 维修价格-家电-热水器
     */
    public static final String RESHUIQI=SERVER_URL+"repair_h5/jiadian_reshuiqi.html";
    /**
     * 维修价格-家电-油烟机
     */
    public static final String YOUYANJI=SERVER_URL+"repair_h5/jiadian_youyanji.html";
    /**
     * 维修价格-家电-消毒柜
     */
    public static final String XIAODUGUI=SERVER_URL+"repair_h5/jiadian_xiaodugui.html";
    /**
     * 维修价格-家电-电脑
     */
    public static final String DIANNAO=SERVER_URL+"repair_h5/jiadian_diannao.html";
    /**
     * 维修价格-家电-空调
     */
    public static final String KONGTIAO=SERVER_URL+"repair_h5/jiadian_kongtiao.html";
    /**
     * 维修价格-家电-洗衣机
     */
    public static final String XIYIJI=SERVER_URL+"repair_h5/jiadian_xiyiji.html";
    /**
     * 维修价格-家电-冰箱
     */
    public static final String BINGXIANG=SERVER_URL+"repair_h5/jiadian_bingxiang.html";
    /**
     * 维修价格-灯具-灯具
     */
    public static final String DENGJU=SERVER_URL+"repair_h5/dengju_dengju.html";
    /**
     * 维修价格-灯具-开关插座
     */
    public static final String KAIGUANCHAZUO=SERVER_URL+"repair_h5/dengju_kaiguanchazuo.html";
    /**
     * 维修价格-灯具-电路
     */
    public static final String DIANLU=SERVER_URL+"repair_h5/dengju_dianlu.html";
    /**
     * 维修价格-其他-开锁换锁
     */
    public static final String KAISUOHUANSUO=SERVER_URL+"repair_h5/other_kaisuohuansuo.html";
    /**
     * 维修价格-其他-管道疏通
     */
    public static final String GUANDAOSHUTONG=SERVER_URL+"repair_h5/other_guandaoshutong.html";
    /**
     * 维修价格-其他-墙面打孔
     */
    public static final String QIANGMIANDAKONG=SERVER_URL+"repair_h5/other_qiangmiandakong.html";
    /**
     * 维修价格-其他-家具
     */
    public static final String JIAJU=SERVER_URL+"repair_h5/other_jiaju.html";
    /**
     * 维修价格-其他-门
     */
    public static final String MEN=SERVER_URL+"repair_h5/other_men.html";
    /**
     * 维修价格-其他-床
     */
    public static final String CHUANG=SERVER_URL+"repair_h5/other_chuang.html";
    /**
     * 维修价格-其他-衣架五金
     */
    public static final String YIJIAWUJIN=SERVER_URL+"repair_h5/other_yijiawujin.html";
    /**
     * 维修价格-其他-防盗网
     */
    public static final String FANGDAOWANG=SERVER_URL+"repair_h5/other_fangdaowang.html";
    /**
     * 维修价格-清洗-燃气灶
     */
    public static final String QINGXI_RANQIZAO=SERVER_URL+"repair_h5/qingxi_ranqizao.html";
    /**
     * 维修价格-清洗-热水器
     */
    public static final String QINGXI_RESHUIQI=SERVER_URL+"repair_h5/qingxi_reshuiqi.html";
    /**
     * 维修价格-清洗-油烟机
     */
    public static final String QINGXI_YOUYANJI=SERVER_URL+"repair_h5/qingxi_youyanji.html";
    /**
     * 维修价格-清洗-空调
     */
    public static final String QINGXI_KONGTIAO=SERVER_URL+"repair_h5/qingxi_kongtiao.html";
    /**
     * 维修价格-清洗-冰箱
     */
    public static final String QINGXI_BINGXIANG=SERVER_URL+"repair_h5/qingxi_bingxiang.html";
    /**
     * 维修价格-清洗-洗衣机
     */
    public static final String QINGXI_XIYIJI=SERVER_URL+"repair_h5/qingxi_xiyiji.html";
    /*选择城市  */
    public static final String SELECT_CITY=SERVER_URL+"api/serve_city/list";
    /*选择地区 */
    public static final String SELECT_DISTRICT=SERVER_URL+"api/serve_city/district";
   /*师傅拍照上传*/
    public static final String Upload_Image=SERVER_URL+"repairman/repair_order/order_image";
    /*已上门开始服务*/
    public static final String  Start_Server=SERVER_URL+"repairman/repair_order/start_serve";
    /*保修卡重写  */
    public static final String  Rewrite_Warranty=SERVER_URL+"repairman/repair_order/warranty_card";
    /*获取价格接口  */
    public static final String  PriceBook_URL=SERVER_URL+"api/serve_city/repairman_app_serve";


    public static final String  Enterprise_List=SERVER_URL+"/repairman/repairman/getEnterprise";
}
