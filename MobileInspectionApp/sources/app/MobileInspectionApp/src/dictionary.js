const Dictionary = {
    Sex: [
        { value: '1', label: '男' },
        { value: '2', label: '女' },
    ],
    //聘用情况
    EmpWay: [
        { value: '1', label: '在聘' },
        { value: '2', label: '解聘' },
    ],
    //是否
    DefaultIF: [
        { value: '1', label: '是' },
        { value: '0', label: '否' },
    ],
    //IF_OLD_DT
    IF_OLD_DT: [
        { value: '1', label: '是' },
        { value: '0', label: '否' },
    ],
    //IF_OLDDED_DT_PG
    IF_OLDDED_DT_PG: [
        { value: '1', label: '是' },
        { value: '0', label: '否' },
    ],
    //是否限速器校验
    IF_LIMIT: [
        { value: '1', label: '有' },
        { value: '0', label: '无' },
    ],
    //是否成功
    IfSucess: [
        { value: '-1', label: '否' },
        { value: '0', label: '是' },
    ],
    //省级国家级
    ApprOrg: [
        { value: '省级', label: '省级' },
        { value: '国家', label: '国家' },
    ],
    //isPDF
    IS_PDFPC: [
        { value: '0', label: '否' },
        { value: '2', label: '是' },
    ],
    //单位信用等级
    UNT_CERDIT: [
        { value: '0', label: '未评定' },
        { value: '1', label: '甲级(A)' },
        { value: '2', label: '乙级(B)' },
        { value: '3', label: '丙级(C)' },
        { value: '4', label: '丁级(D)' },
    ],
    //监察人员性质
    JcMenType: [
        { value: '1', label: '监察' },
        { value: '2', label: '办证' },
        { value: '3', label: '稽查' },
        { value: '4', label: '其它' },
    ],
    //完成状态
    FinishStatus: [
        { value: '未实施', label: '未实施' },
        { value: '已实施', label: '已实施' },
    ],
    //受理是否需要评审
    AccNeedApp: [
        { value: '1', label: '不需要鉴定评审' },
        { value: '2', label: '需要鉴定评审' },
        { value: '3', label: '需要型式试验' },
    ],
    //检查主要内容
    CHK_TYPE: [
        { value: '管理情况', label: '管理情况' },
        { value: '抽查设备安全状况', label: '抽查设备安全状况' },
        { value: '其他', label: '其他' },
    ],
    //检查单位类型
    DICT_CHK_UNT_TYPE: [
        { value: '设计', label: '设计' },
        { value: '制造', label: '制造' },
        { value: '安装', label: '安装' },
        { value: '改造', label: '改造' },
        { value: '维修', label: '维修' },
        { value: '使用', label: '使用' },
        { value: '气瓶充装', label: '气瓶充装' },
        { value: '其他', label: '其他' },
    ],
    //现场监察处理措施
    DICT_TASK_ACTIONS: [
        { value: '下达监察指令书', label: '下达监察指令书' },
        { value: '实施查封', label: '实施查封' },
        { value: '实施扣押', label: '实施扣押' },
        { value: '其他', label: '其他' },
    ],
    //评审报告录入--鉴定评审项目
    RVE_ITEMS: [
        { value: '1', RVE_ITEM_NAME: '许可资源条件' },
        { value: '2', RVE_ITEM_NAME: '质量保证体系建立与实施' },
        { value: '3', RVE_ITEM_NAME: '相关技术资料审查' },
        { value: '4', RVE_ITEM_NAME: '产品（设备）安全性能抽查检验' },
        { value: '5', RVE_ITEM_NAME: '型式试验情况' },
    ],
    //整改类型
    DICT_OVERHAUL_TYPE: [
        { value: '1', label: '立即整改' },
        { value: '2', label: '限期整改' },
    ],
    //指令书状态
    DICT_OVERHAUL_STATUS: [
        { value: '0', label: '未反馈' },
        { value: '1', label: '待确认' },
        { value: '2', label: '待复查' },
        { value: '3', label: '逾期未整改' },
        { value: '4', label: '已整改' },
    ],
    //查封扣押存在问题
    DICT_SEIZURE_PROBLEM: [{ value: '特种设备不符合安全技术规范要求' }, { value: '特种设备存在严重事故隐患' }, { value: '流入市场的特种设备达到报废条件' }, { value: '流入市场的特种设备已经报废' }],
    //设计单位岗位项目
    DES_POSITION: [{ value: '设计技术负责人' }, { value: '设计审批人员' }, { value: '设计审核人员' }, { value: '设计校核人员' }, { value: '设计人员' }],
    //充装单位岗位项目
    FILL_POSITION: [{ value: '负责人(站长)' }, { value: '技术负责人' }, { value: '安全管理员' }, { value: '检查员' }, { value: '充装员' }, { value: '化验员' }, { value: '检修员' }, { value: '辅助人员' }],
    //制造安装改造维修 单位 岗位项目
    MAK_INS_POSITION: [
        { value: '质量保证工程师' },
        { value: '设计责任人员' },
        { value: '材料责任人员' },
        { value: '工艺责任人员' },
        { value: '焊接责任人员' },
        { value: '理化责任人员' },
        { value: '热处理责任人员' },
        { value: '无损检测责任人员' },
        { value: '检验与试验责任人员' },
        { value: '设备和检验与试验装置责任人员' },
        { value: '机械加工责任人员' },
        { value: '金属结构制作责任人员' },
        { value: '电控系统制作责任人员' },
        { value: '质量检验人员' },
        { value: '无损检测人员' },
        { value: '焊接作业人员' },
        { value: '安装调试作业人员' },
    ],
    //检验岗位项目
    CHK_POSITION: [{ value: '质量负责人' }, { value: '技术负责人' }, { value: '检验责任师' }, { value: '检验员' }],
    //使用单位登记申请部门类型
    DEPT_TYPE: [
        { value: '0', label: '无内设管理部门' },
        { value: '2', label: '内设管理部门' },
        { value: '1', label: '分支机构' },
    ],
    //电子证照字典
    DZZZ_LIST: [
        { value: 'INS', label: '安装改造维修许可证' },
        { value: 'MAK', label: '制造许可证' },
        { value: 'DES', label: '设计许可证' },
        { value: 'CHK', label: '检验检测机构核准证' },
        { value: 'FIL_YLRQ', label: '移动式压力容器充装许可证' },
        { value: 'FIL_QP', label: '气瓶充装许可证' },
        { value: 'EQP', label: '设备使用登记证' },
        { value: 'MEN', label: '作业人员证' },
    ],
    //游乐设施设备级别
    DICT_AMU_LEVEL: [
        { value: 'A', label: 'A' },
        { value: 'B', label: 'B' },
        { value: 'C', label: 'C' },
    ],
    //单位许可申请保存提示信息
    UNT_APL_SAVE_MSG: '可在【许可申请查询】中查询到当前保存的申请单记录！点击【详细】继·续编辑！',
    //单位许可证级别特殊字符
    TESHU_CHAR: ['≥', '＞', '≤', '＜', 'MPa', 'm/s', 'km/s', 'm', '°', '·', '、', 'PN', 'DN'],
    //充装单位许可级别特殊字符
    TESHU_CHAR_FILL: ['MPa', '℃', '㎡', 'm3', 'φ', 'Ⅰ', 'Ⅱ', 'Ⅲ'], //待定
    //单位类型
    DICT_UNT_TYPE: [
        { value: '1', label: '安装改造维修单位' },
        { value: '2', label: '制造单位' },
        { value: '3', label: '设计单位' },
        { value: '4', label: '充装单位' },
        { value: '5', label: '检验机构' },
    ],
    //证书类型
    DICT_CERT_TYPE: [
        { value: '1', label: '安装改造维修许可证' },
        { value: '2', label: '制造许可证' },
        { value: '3', label: '设计许可证' },
        { value: '4', label: '充装许可证' },
        { value: '5', label: '检验机构核准许可证' },
    ],
    //压力容器--压力类别
    DICT_CONSORT: [
        { value: 'Ⅰ类', label: 'Ⅰ类' },
        { value: 'Ⅱ类', label: 'Ⅱ类' },
        { value: 'Ⅲ类', label: 'Ⅲ类' },
        { value: '无', label: '无' },
    ],
    //压力容器--压力类别
    DICT_PRESORT: [
        { value: '低压(L)', label: '低压(L)' },
        { value: '中压(M)', label: '中压(M)' },
        { value: '高压(H)', label: '高压(H)' },
        { value: '超高压(U)', label: '超高压(U)' },
        { value: '无', label: '无' },
    ],
    //设备进口类型
    IMPORT_TYPE: [
        { value: '国产', label: '国产' },
        { value: '部件进口', label: '部件进口' },
        { value: '整机进口', label: '整机进口' },
    ],
    //单位许可编辑提交资料备注信息
    UNT_FILE_UPLOAD_MEMO: '注：1.提交的纸质许可申请材料，不要求把每1页都上传电子附件，把封面盖章页上传即可。<br/>2.人员方面也可以只上传加盖公章的汇总信息封面（汇总有关证书、合同、社保凭证装订成册并在封面上载明汇总信息）。<br/>3.上传电子附件是表明向窗口提交了哪些纸质申请材料。<br/>4.申请书一式四份可不上传，预审通过后打印出来提交窗口。',
    //其他类型的短信
    SMS_OTHER_TYPE: [
        { value: '11', label: '单位许可受理决定书' },
        { value: '12', label: '单位许可决定书' },
        { value: '21', label: '设备许可受理决定书' },
        { value: '22', label: '设备许可决定书' },
        { value: '31', label: '人员许可受理决定书' },
        { value: '32', label: '人员许可决定书' },
    ],
    //短信配置标签
    SMS_LABEL: [
        { value: '1', label: '{手机}' },
        { value: '2', label: '{姓名}' },
    ],
    //锅炉是否立式
    DICT_IF_VERTICAL_GL: [{ value: '立式' }, { value: '卧式' }],
    //锅炉结构型式
    DICT_MAINSTRFORM_GL: [{ value: '锅壳' }, { value: '水管' }, { value: '盘管' }],
    //锅炉房类型
    DICT_STOKEHOLDTYPE: [{ value: '独立' }, { value: '地下' }, { value: '中间' }, { value: '顶层' }],
    //加热方式
    DICT_HEATUPMODE: [{ value: '燃煤' }, { value: '燃油' }, { value: '燃气' }, { value: '电加热' }, { value: '余热' }, { value: '其他' }],
    //燃料种类
    DICT_BURNINGTYPE: [{ value: '无烟煤' }, { value: '烟煤' }, { value: '褐煤' }, { value: '煤矸石' }, { value: '柴油' }, { value: '重油' }, { value: '渣油' }, { value: '天然气' }, { value: '管道液化气' }, { value: '城市煤气' }, { value: '高炉煤气' }, { value: '电加热' }, { value: '余热' }, { value: '再生生物资' }, { value: '黑液' }, { value: '垃圾' }],
    //燃烧方式
    DICT_BURNMODE: [{ value: '链条炉排' }, { value: '固定炉排' }, { value: '振动炉排' }, { value: '往复炉排' }, { value: '室燃' }, { value: '流化床' }],
    //水处理方式
    DICT_WATERDEALTYPE: [{ value: '锅内' }, { value: '锅外' }, { value: '无水处理' }],
    //除氧方式
    DICT_DEOXIDIZEMODE: [{ value: '热力除氧' }, { value: '真空除氧' }, { value: '解析除氧' }, { value: '化学药剂除氧' }, { value: '催化树脂除氧' }, { value: '无除氧措施' }],
    //出渣方式
    DICT_DROSSTYPE: [{ value: '机械' }, { value: '人工' }],
    //消烟除尘方式
    DICT_SOOTAVOIDMODE: [{ value: '旋风除尘' }, { value: '电气除尘' }, { value: '水幕除尘' }, { value: '布袋除尘' }],

    //压力容器
    //主体结构型式
    DICT_MAINSTRFORM: [{ value: '固定式' }, { value: '半挂式' }, { value: '独立容器' }, { value: '长管组' }, { value: '瓶组' }],
    //保温（绝热形式）
    DICT_TEMPPREMODE: [{ value: '保温材料' }, { value: '真空绝热' }],
    //介质装卸方式
    DICT_MEDIASSEMODE: [{ value: '动力装卸' }, { value: '整体装卸' }],
    //封头型式
    DICT_SEALTYPE: [{ value: '标准椭圆' }, { value: '椭圆' }, { value: '球型' }, { value: '碟形' }, { value: '锥形' }, { value: '凸型' }, { value: '平盖' }, { value: '椭圆+锥形' }, { value: '椭圆+平盖' }],
    //支座型式
    DICT_BASESTYLE: [{ value: '支承式' }, { value: '鞍座式' }, { value: '裙座式' }, { value: '悬挂式' }, { value: '转轴支承式' }, { value: '支脚式' }, { value: '圈座式' }, { value: '框架式' }, { value: '立式支承式' }, { value: '卧式支承式' }],
    //监检形式
    DICT_INSPECTFORM: [{ value: '监造' }, { value: '口岸检验' }, { value: '型式试验' }, { value: '其他监检形式' }],

    //压力管道
    //铺设方式
    DICT_LAY_MODE: [{ value: '埋地' }, { value: '架空' }, { value: '埋地 +架空' }, { value: '无' }],
    //管道级别
    DICT_PIPELINE_LEVEL: [{ value: 'GA1' }, { value: 'GA2' }, { value: 'GB1' }, { value: 'GB2' }, { value: 'GC1' }, { value: 'GC2' }, { value: 'GC3' }],
    //管道材料
    DICT_METERIAL: [{ value: 'PE管' }, { value: '钢制' }],

    //电梯
    //拖动方式
    DICT_DRAG_MODE: [{ value: '交流单速' }, { value: '交流双速' }, { value: '变极调速' }, { value: '交流调压调速' }, { value: '交流变频' }, { value: '直流晶闸管直接' }, { value: '柱塞直顶' }, { value: '柱塞侧置' }],
    //补偿方式
    DICT_COMPENTYPE: [{ value: '补偿链' }, { value: '补偿绳' }, { value: '补偿缆' }],
    //对重导轨型式
    DICT_COUNORBTYPE: [{ value: 'T型导轨' }, { value: '空心导轨' }, { value: '热轧型钢导轨' }],

    //起重机械
    //工作级别
    DICT_WORKGRADE: [{ value: 'A1' }, { value: 'A2' }, { value: 'A3' }, { value: 'A4' }, { value: 'A5' }, { value: 'A6' }, { value: 'A7' }, { value: 'A8' }],
    //工作环境
    DICT_WORKCONDITION: [{ value: '露天' }, { value: '非露天' }, { value: '有毒' }, { value: '高温' }, { value: '粉尘' }, { value: '其他工作环境' }],
    //取物装置
    DICT_FETCHSET: [{ value: '吊钩' }, { value: '起重电磁铁' }, { value: '起重真空吸盘' }, { value: '抓斗' }, { value: '集装箱专用吊具' }, { value: '其他取物装置' }],
    //操纵方式
    DICT_OPER_STYTLE: [{ value: '凸轮开关' }, { value: '主令开关' }, { value: '按钮' }, { value: '摇控' }, { value: '倒顺开关' }, { value: '手动' }, { value: '其他操纵方式' }],

    //机动车辆
    //动力方式
    DICT_DYNAMICMODE: [{ value: '内燃机' }, { value: '电动机' }, { value: '其它' }],
    //燃料种类
    DICT_BURKIN: [{ value: '汽油' }, { value: '柴油' }, { value: '天然气' }, { value: '液化石油气' }, { value: '蓄电池' }, { value: '汽油液化石油气' }, { value: '柴油液化石油气' }, { value: '其它' }],
    //颜色
    DICT_COLOR: [{ value: '白' }, { value: '铝白' }, { value: '银灰' }, { value: '黄' }],
    //车辆类型
    DICT_CARTYPE: [{ value: '小车' }, { value: '小汽车' }, { value: '卡车' }],
    //证书级别
    CERT_LEV: [{ value: 'A级' }, { value: 'B级' }, { value: 'C级' }, { value: 'D级' }, { value: 'A1' }, { value: 'A2' }, { value: 'B' }, { value: '样机试制' }, { value: '试安装' }, { value: '1级' }, { value: '2级' }, { value: '3级' }, { value: '/' }],
    //用户状态
    USER_STATUS: [
        { value: '1', label: '未启用' },
        { value: '2', label: '已启用' },
        { value: '3', label: '删除' },
    ],
    //人员属性
    USER_ISP_TYPE: [
        { value: '1', label: '机电' },
        { value: '2', label: '承压' },
        { value: '3', label: '综合' },
    ],
    //职位
    POST: [
        { value: '1', label: '部长' },
        { value: '2', label: '科长' },
        { value: '3', label: '科员' },
    ],
    //编制情况
    STAFF_TYPE: [
        { value: '行政编', label: '行政编' },
        { value: '事业编', label: '事业编' },
        { value: '非编', label: '非编' },
    ],
    //角色类型
    ROLE_TYPE: [
        { value: '1', label: '流程实体' },
        { value: '2', label: '一级权限' },
        { value: '3', label: '二级权限' },
    ],
    //业务大类
    BUSI_TYPE: [
        { value: '1', label: '法定业务' },
        { value: '2', label: '委托业务' },
    ],
    //流程节点配置字典
    FLOW_ROLE_TYPE: [
        { value: '1001', label: '报告编制' },
        { value: '1002', label: '责任工程师审核' },
        { value: '1003', label: '负责人审批' },
        { value: '1004', label: '文印室打印' },
    ],
    //协议状态
    PROTOCOL_STATUS: [
        { value: '-1', label: '删除协议' },
        { value: '0', label: '协议申报' },
        { value: '1', label: '协议待审核' },
        { value: '3', label: '网上报检资料提交' },
        { value: '4', label: '业务受理申请' },
        { value: '5', label: '业务受理终结' },
    ],
    //委托协议状态
    JF_PROTOCOL_STATUS: [
        { value: '-1', label: '删除协议' },
        { value: '1', label: '协议待审核' },
        { value: '4', label: '业务受理申请' },
        { value: '5', label: '业务受理终结' },
        { value: '6', label: '业务终结' },
    ],

    //审核状态
    CHK_STA: [
        { value: '0', label: '未审核' },
        { value: '1', label: '已审核' },
    ],
    //过程报检审核
    CHK_STA_PROCESS: [
        { value: '0', label: '未审核' },
        { value: '1', label: '已审核' },
        { value: '2', label: '二级审核' },
    ],
    //审核意见
    CHK_OPIN: [
        { value: '1', label: '审核通过' },
        { value: '-1', label: '审核不通过' },
    ],
    //审批意见
    APPR_OPIN: [
        { value: '1', label: '审批通过' },
        { value: '-1', label: '审批不通过' },
    ],
    //报告书修订审批意见
    ZGAPPR_OPTION: [
        { value: '1', label: '重新出具检验报告' },
        { value: '-1', label: '审批不通过' },
        { value: '2', label: '出具更改说明单(不修改平台数据)' },
        { value: '3', label: '出具更改说明单(修改平台数据)' },
    ],
    //移装类型
    MOVE_TYPE: [
        { value: '0', label: '省外移装' },
        { value: '1', label: '跨登记区域移装' },
        { value: '3', label: '登记区域内移装' },
    ],
    //许可种类
    PERMIT_TYPE_COD: [
        { value: '1', label: '安装改造维修' },
        { value: '2', label: '制造' },
        { value: '3', label: '设计' },
    ],
    //颁发单位
    APPR_ORG: [{ value: '省级' }, { value: '国家' }],
    //自定义的施工类型，区分业务类型
    BUI_TYPE: [
        { value: '1', label: '普通业务' },
        { value: '2', label: '试安装' },
        { value: '3', label: '移装' },
    ],
    //检测依据
    JCYJ: [
        { value: '1', label: '特种设备安全法' },
        { value: '2', label: '特种设备安全监察条例' },
    ],
    //关联老设备
    ASS_TYPE: [
        { value: '1', label: '设备相关' },
        { value: '3', label: '设备无关' },
    ],

    //任务库
    TASK_DATABASE: [
        { value: '0', label: '制造业务' },
        { value: '1', label: '普通业务' },
        { value: '2', label: '单独报告' },
    ],
    TASK_DATABASE1: [
        { value: '1', label: '普通业务' },
        { value: '0', label: '制造业务' },
    ],
    TASK_DATABASE2: [
        { value: '0', label: '制造业务' },
        { value: '1', label: '普通业务' },
        { value: '2', label: '单独报告' },
    ],
    //任务分配状态
    TASK_ALLO_STA: [
        { value: '0', label: '部门分配' },
        { value: '1', label: '科室分配' },
        { value: '2', label: '责任人派工' },
        { value: '3', label: '分配派工完成' },
    ],
    //设备范畴属性
    ISP_TYPE: [
        { value: '1', label: '机电类' },
        { value: '2', label: '承压类' },
        { value: '3', label: '其它类' },
    ],
    //业务属性
    BUSI_TYPE: [
        { value: '1', label: '法定业务' },
        { value: '2', label: '委托业务' },
    ],
    //延期/超期类型
    ABNOR_TYPE: [
        { value: '1', label: '延期检验' },
        { value: '2', label: '超期未检' },
    ],
    //超期未检类型
    UNISP_TYPE: [
        { value: '1', label: '已检正在出具检验报告' },
        { value: '2', label: '已约定检验时间' },
        { value: '3', label: '已上报监察机构' },
        { value: '9', label: '手工报告单上报监察机构' },
        { value: '4', label: '正在确认未检原因' },
        { value: '5', label: '设备状态已变更未提交变更申请' },
        { value: '6', label: '因保温层未拆除或流动式起重机无法实施检验' },
        { value: '7', label: '备用锅炉或季节性使用锅炉' },
        { value: '8', label: '其它' },
        { value: '99', label: '空' },
    ],
    UNISP_TYPE_SUB: [
        { value: '1', label: '已检正在出具检验报告' },
        { value: '2', label: '已约定检验时间' },
        { value: '9', label: '手工报告单上报监察机构' },
        { value: '4', label: '正在确认未检原因' },
        { value: '5', label: '设备状态已变更未提交变更申请' },
        { value: '6', label: '因保温层未拆除或流动式起重机无法实施检验' },
        { value: '7', label: '备用锅炉或季节性使用锅炉' },
        { value: '8', label: '其它' },
    ],

    //使用状态
    EQP_USE_STA: [
        { value: '1', label: '未投入使用' },
        { value: '2', label: '在用' },
        { value: '3', label: '停用' },
        { value: '4', label: '报废' },
        { value: '5', label: '拆除' },
        { value: '6', label: '迁出' },
        { value: '7', label: '垃圾数据' },
        { value: '9', label: '在用未注册' },
    ],
    //使用状态,显示删除
    EQP_USE_STA_ALL: [
        { value: '1', label: '未投入使用' },
        { value: '2', label: '在用' },
        { value: '3', label: '停用' },
        { value: '4', label: '报废' },
        { value: '5', label: '拆除' },
        { value: '6', label: '迁出' },
        { value: '7', label: '垃圾数据' },
        { value: '8', label: '删除(移除监察)' },
        { value: '9', label: '在用未注册' },
    ],

    //注册状态
    EQP_REG_STA: [
        { value: '0', label: '待注册' },
        { value: '1', label: '在册' },
        { value: '3', label: '注销登记' },
    ],
    //安全评定等级
    SAFE_LEV: [
        { value: '1', label: '1级' },
        { value: '2', label: '2级' },
        { value: '3', label: '3级' },
        { value: '4', label: '4级' },
        { value: '5', label: '5级' },
    ],
    //事故隐患类别
    ACCI_TYPE: [
        { value: '1', label: '特别重大' },
        { value: '2', label: '特大' },
        { value: '3', label: '重大' },
        { value: '4', label: '严重' },
        { value: '5', label: '一般' },
    ],
    //设备使用场所类型
    EQP_USE_PLACE: [
        { value: '1', label: '公众聚集场所（学校）' },
        { value: '2', label: '公众聚集场所（幼儿园）' },
        { value: '3', label: '公众聚集场所（医院）' },
        { value: '4', label: '公众聚集场所（车站）' },
        { value: '5', label: '公众聚集场所（客运码头）' },
        { value: '6', label: '公众聚集场所（商场）' },
        { value: '7', label: '公众聚集场所（体育场馆）' },
        { value: '8', label: '公众聚集场所（展览馆）' },
        { value: '9', label: '公众聚集场所（公园）' },
        { value: '10', label: '公众聚集场所（其它）' },
        { value: '11', label: '住宅' },
    ],
    //目录属性
    IN_CAG: [
        { value: '2', label: '目录外' },
        { value: '1', label: '目录内' },
    ],
    //审核结果
    CHK_OPTION: [
        { value: '', label: '未审核' },
        { value: '-1', label: '审核不通过' },
        { value: '1', label: '审核通过' },
    ],
    EQPSTACHG_CHK_OPTION: [
        { value: '', label: '未审核' },
        { value: '0', label: '审核不通过' },
        { value: '1', label: '审核通过' },
    ],
    EQPSTACHG_CHK_OPTIONALL: [
        { value: '3', label: '未审核' },
        { value: '0', label: '审核不通过' },
        { value: '1', label: '审核通过' },
        { value: '2', label: '已提交监察机构' },
    ],
    EQPPIPECHG_CHK_OPTIONALL: [
        { value: '', label: '未审核' },
        { value: '0', label: '审核不通过' },
        { value: '1', label: '已提交监察机构' },
        { value: '2', label: '审核通过' },
        { value: '3', label: '监察不通过' },
        { value: '4', label: '自动审核通过' },
    ],
    EQPUNTCHG_CHK_OPTIONALL: [
        { value: '0', label: '未审核' },
        { value: '-1', label: '审核不通过' },
        { value: '1', label: '已提交监察机构' },
        { value: '2', label: '审核通过' },
        { value: '3', label: '监察不通过' },
        { value: '4', label: '自动审核通过' },
        { value: '9', label: '作废' },
    ],
    EQPUNTCHG_CHK_OPTION: [
        { value: '0', label: '未审核' },
        { value: '-1', label: '审核不通过' },
        { value: '1', label: '审核通过' },
    ],

    UNT_CHG_TYPE: [
        { value: '1', label: '检验单位修改' },
        { value: '3', label: '新增单位' },
    ], //,{value:'2',label:'新增监察单位'},{value:'3',label:'检验新增单位'}
    UNTCHG_CHK_OPTIONALL: [
        { value: '0', label: '未审核' },
        { value: '-1', label: '审核不通过' },
        { value: '1', label: '已提交监察机构' },
        { value: '2', label: '审核通过' },
        { value: '3', label: '监察不通过' },
        { value: '4', label: '自动审核通过' },
        { value: '9', label: '作废' },
    ],
    UNTCHG_CHK_OPTION: [
        { value: '0', label: '未审核' },
        { value: '-1', label: '审核不通过' },
        { value: '1', label: '审核通过,提交监察机构' },
    ],

    //没有不通过的审核结果
    CHK_NOBTGOPTION: [
        { value: '', label: '未审核' },
        { value: '1', label: '审核通过' },
    ],
    //状态变更申请单类型
    EQP_APL_TYPE: [
        { value: '企业提交申请', label: '企业提交申请' },
        { value: '检验员资料确认', label: '检验员资料确认' },
        { value: '第三方确认', label: '第三方确认' },
    ],
    APL_TYPE: [{ value: '2', label: '重新挂接' }], //{ value: '1', label: '单位信息变更' },
    //发票状态
    INVC_STA: [
        { value: '0', label: '未开' },
        { value: '1', label: '已开' },
        { value: '2', label: '作废' },
        { value: '3', label: '预开' },
        { value: '4', label: '遗失' },
    ],
    //票字第号
    INVC_COD_COD: [
        { value: '01', label: '票字第01号' },
        { value: '02', label: '票字第02号' },
        { value: '03', label: '票字第03号' },
        { value: '04', label: '票字第04号' },
        { value: '05', label: '票字第05号' },
        { value: '06', label: '票字第06号' },
        { value: '07', label: '票字第07号' },
        { value: '08', label: '票字第08号' },
        { value: '09', label: '票字第09号' },
    ],
    INVC_COD_COD1: [
        { codvalue: '01', codlabel: '票字第01号' },
        { codvalue: '02', codlabel: '票字第02号' },
        { codvalue: '03', codlabel: '票字第03号' },
        { codvalue: '04', codlabel: '票字第04号' },
        { codvalue: '05', codlabel: '票字第05号' },
        { codvalue: '06', codlabel: '票字第06号' },
        { codvalue: '07', codlabel: '票字第07号' },
        { codvalue: '08', codlabel: '票字第08号' },
        { codvalue: '09', codlabel: '票字第09号' },
    ],
    //回款状态
    ACCP_STA: [
        { value: '0', label: '未回款' },
        { value: '1', label: '全额回款' },
        { value: '2', label: '部分回款' },
    ],
    //项目种类票类(1机考 2机检 3承压考 4承压检 5技术服务项目 6网上报检发票 7国有资产有偿使用收入)
    INVPRO_TYPE: [
        { value: '1', label: '机电类考试项目含技能鉴定' },
        { value: '2', label: '机电类检验项目' },
        { value: '3', label: '承压类考试项目含技能鉴定' },
        { value: '4', label: '承压类检验项目' },
        { value: '5', label: '技术服务/其它项目' },
        { value: '7', label: '其它法定收入' },
    ],
    //ifLegar区分项目种类
    INVPRO_TYPE1: [
        { value: '1', label: '机电类考试项目含技能鉴定' },
        { value: '2', label: '机电类检验项目' },
        { value: '3', label: '承压类考试项目含技能鉴定' },
        { value: '4', label: '承压类检验项目' },
        { value: '7', label: '其它法定收入' },
    ],
    INVPRO_TYPE2: [{ value: '5', label: '技术服务/其它项目' }],
    CONFM_FLAG: [
        { value: '0', label: '待确认' },
        { value: '1', label: '已确认' },
    ],
    //报告节点
    CURR_NODE_COMBOX: [
        { value: '101', label: '报告编制' },
        { value: '102', label: '责任工程师审核' },
        { value: '104', label: '负责人审批' },
        { value: '105', label: '文印室打印' },
        { value: '106', label: '终结' },
        { value: '107', label: '注销' },
    ],
    //报告节点全部
    CURR_NODE_ALL_COMBOX: [
        { value: '101', label: '报告编制' },
        { value: '102', label: '责任工程师审核' },
        { value: '103', label: '等待复检' },
        { value: '104', label: '负责人审批' },
        { value: '105', label: '文印室打印' },
        { value: '106', label: '终结' },
        { value: '107', label: '注销' },
    ],
    //报告待办状态
    //发票关联标志 -1：可不关联发票，0：未关联发票，1:已关联发票，发票未审核；2:已关联发票，发票已审核，20：免关联发票申请，21：免关联发票申请审核通过
    ASSINV_FALG: [
        { value: '-1', label: '可不关联发票' },
        { value: '0', label: '未关联发票' },
        { value: '1', label: '已关联发票，发票未审核' },
        { value: '2', label: '已关联发票，发票已审核' },
        { value: '20', label: '免关联发票申请' },
        { value: '21', label: '免关联发票申请审核通过' },
    ],
    //通用审核状态
    COMM_STATUS: [
        { value: '-1', label: '删除' },
        { value: '0', label: '待办' },
        { value: '1', label: '待审核' },
        { value: '2', label: '审核完成' },
    ],
    COMM_STATUS2: [
        { value: '-1', label: '删除' },
        { value: '0', label: '待办' },
        { value: '1', label: '待审核' },
        { value: '2', label: '完成' },
        { value: '3', label: '待审批' },
        { value: '4', label: '待技质部审批' },
        { value: '5', label: '待打印报告说明单' },
        { value: '6', label: '待打印原始记录说明单' },
    ],
    CURR_STATUS: [
        { value: '1', label: '待办' },
        { value: '0', label: '已办' },
    ],
    DRAFT_STA: [
        { value: '1', label: '申请表导入' },
        { value: '2', label: '申请表校对' },
        { value: '3', label: '申请表开票' },
        { value: '4', label: '开票完成' },
        { value: '5', label: '作废' },
    ],
    DRAFT_INVCTYPE: [
        { value: '1904', label: '福建省政府非税收入缴款通知书' },
        { value: '1901', label: '福建省政府非税收入票据（电子版）' },
        { value: '2900', label: '税务票' },
    ],
    //,{value:'26900',label:'非税纸质票据'}
    NON_DRAFT_INVCTYPE: [
        { value: '1904', label: '福建省政府非税收入缴款通知书' },
        { value: '1901', label: '福建省政府非税收入票据（电子版）' },
    ],
    //,{value:'1902',label:'福建省政府非税收入票据（手工版）'}
    NOFAX_DRAFT_STA: [
        { value: '2', label: '申请表校对' },
        { value: '6', label: '申请表待审核' },
        { value: '3', label: '申请表开票' },
        { value: '4', label: '开票完成' },
        { value: '5', label: '发票作废' },
    ],
    REMIT_STA: [
        { value: '1', label: '未汇缴' },
        { value: '2', label: '已汇缴' },
    ],
    //协议的收费方式
    PAY_TYPE: [
        { value: '1', label: '银行转账' },
        { value: '2', label: '现金收取' },
        { value: '3', label: '免收费' },
    ],
    //申报类型
    PROCESS_TYPE: [
        { value: '10', label: '进场申报' },
        { value: '20', label: '耐压试验' },
        { value: '25', label: '隐蔽工程申报' },
        { value: '30', label: '验收申报' },
        { value: '50', label: '中止监检' },
        { value: '40', label: '其他' },
    ],
    //管理部门类型
    D_MGE_DEPT_TYPE: [
        { value: '0', label: '无内设管理部门' },
        { value: '1', label: '内设管理部门' },
        { value: '2', label: '内设分支机构' },
    ],
    //楼盘性质 楼盘性质。
    D_INST_BUILD_TYPE: [{ value: '商品房' }, { value: '复建房' }, { value: '拆迁安置房' }, { value: '廉租房' }, { value: '回迁房' }, { value: '经济适用房' }, { value: '限价房' }, { value: '棚户区' }, { value: '/' }],

    //报告发放方式
    D_SENDTYPE: [{ value: '自取' }, { value: '快递' }, { value: '挂号' }, { value: '邮政小包' }, { value: '代领' }],
    //证书发放付款方式
    D_CERTSEND_PAY_TYPE: [{ value: '收件人付' }, { value: '检验机构付' }],
    //不合格原因
    D_NOTELIGIBLE_REASON: [
        { value: '1', label: '维保单位不合格' },
        { value: '2', label: '使用单位不合格' },
    ],
    D_NOTELIGIBLE_REASON_q: [
        { value: '0', label: '空' },
        { value: '维保单位不合格', label: '维保单位不合格' },
        { value: '使用单位不合格', label: '使用单位不合格' },
        { value: '维保单位及使用单位均不合格', label: '维保单位及使用单位均不合格' },
    ],
    //重要事项类别
    D_IMPORTCASE_TYPE: [
        { value: 'A', label: 'A类' },
        { value: 'B', label: 'B类' },
    ],
    //用于手工报告单的提交
    D_IMPORTCASE_RTYPE: [
        { value: 'A205', label: '检验不合格已上报' },
        { value: 'A204', label: '超期未检已上报' },
    ],
    //重要事项状态
    D_IMPORTCASE_STATUS: [{ value: '空白' }, { value: '待提交' }, { value: '退回' }, { value: '排查' }, { value: '提交' }, { value: '删除' }, { value: '已归并' }, { value: '正处理' }, { value: '待处理' }, { value: '闭环' }],
    //检验结论
    D_ISP_CONCLU: [
        { value: '未出结论', label: '所有' },
        { value: '合格', label: '机电，管道' },
        { value: '复检合格', label: '机电' },
        { value: '不合格', label: '机电，管道' },
        { value: '复检不合格', label: '机电' },
        { value: '符合要求', label: '锅炉容器' },
        { value: '基本符合要求', label: '锅炉容器' },
        { value: '不符合要求', label: '锅炉容器' },
        { value: '该安全阀经校验不合格，不得投入使用', label: '安全阀' },
        { value: '该安全阀经校验合格', label: '安全阀' },
        { value: '1级', label: '管道' },
        { value: '2级', label: '管道' },
        { value: '3级', label: '管道' },
        { value: '4级', label: '管道' },
    ],
    //关系运算符
    FILTER_TYPE: [
        { value: ':', label: '等于' },
        { value: '<>', label: '不等于' },
        { value: '>', label: '大于' },
        { value: '<', label: '小于' },
        { value: '>:', label: '大于等于' },
        { value: '<:', label: '小于等于' },
        { value: 'LIKE', label: '模糊匹配' },
        { value: 'IN', label: '包含' },
        { value: 'NOT IN', label: '不包含' },
    ],
    //逻辑预算符
    RELA_OPE: [
        { value: 'AND', label: '并且' },
        { value: 'OR', label: '或者' },
    ],
    //任务状态
    D_TASK_STA: [
        { value: '0', label: '未派工' },
        { value: '1', label: '已派工' },
        { value: '4', label: '已完成' },
        { value: '5', label: '作废' },
    ],
    //中止监检操作任务状态
    D_TASK_STA_END: [
        { value: '0', label: '未派工' },
        { value: '1', label: '已派工' },
    ],
    //REP_LOGUPDATE
    REPUPDATE_EFF_STA: [
        { value: '0', label: '失效' },
        { value: '1', label: '有效' },
    ],
    REPUPDATE_END_TAG: [
        { value: '0', label: '否' },
        { value: '1', label: '是' },
    ],
    TRAN_TYPE: [
        { value: '0', label: '检验新增设备后更新eqpcod关联信息到监察' },
        { value: '10', label: '从数据库更新设备信息和参数信息' },
        { value: '11', label: '更新设备信息到监察' },
        { value: '12', label: '更新业务状态到监察' },
        { value: '20', label: '更新检验信息到监察' },
        { value: '21', label: '延期拒检检验表的更新' },
        { value: '30', label: '更新单位信息到监察' },
    ],
    ENDINCP_MEMO: [
        { value: '0', label: '告知半年后未安装' },
        { value: '1', label: '安装过程因客观因素停工' },
        { value: '2', label: '设备安装基本完成未申请竣工验收' },
        { value: '3', label: '各类试验不合格3个月内未整改' },
        { value: '9', label: '其他' },
    ],
    //是否中文
    DefaultIF_CHS: [
        { value: '是', label: '是' },
        { value: '否', label: '否' },
    ],
    //收费标准
    FEE_FIRST_NAME: [
        { value: '1', label: '承压类特种设备检验收费项目和标准' },
        { value: '2', label: '机电类特种设备检验收费项目和标准' },
        { value: '3', label: '其他检验收费项目和标准' },
        { value: '4', label: '特种设备作业人员考试收费项目和标准' },
    ],
    //数据管理
    TABLE_DATA: [
        { value: '1', label: '设备台账' },
        { value: '2', label: '任务管理' },
        { value: '3', label: '业务处理信息' },
    ],
    RISP_PER_TAG: [
        { value: '0', label: '不参与考核' },
        { value: '1', label: '已注册设备的参与考核' },
        { value: '2', label: '未注册设备的参与考核' },
        { value: '9', label: '安全阀校验台导入报告' },
    ],
    D_RISP_PER_RESULT: [
        { value: '1001', label: '检验完成无复检' },
        { value: '1002', label: '检验完成有复检' },
        { value: '1003', label: '因设备报停完成' },
        { value: '1004', label: '因设备报废完成' },
        { value: '1005', label: '因设备拆除完成' },
        { value: '1006', label: '因设备迁出完成' },
        { value: '1007', label: '因设备转垃圾数据完成' },
        { value: '1008', label: '因设备转目录外完成' },
        { value: '1009', label: '其他检验机构完成' },
        { value: '1010', label: '因出具手工报告完成' },
        { value: '2001', label: '延期检验' },
        { value: '2002', label: '因转监检完成' },
        { value: '2003', label: '因转定检完成' },
        { value: '2004', label: '中止监检' },
        { value: '3001', label: '已检正在出具检验报告' },
        { value: '3002', label: '已约定检验时间' },
        { value: '3003', label: '已上报监察机构' },
        { value: '3004', label: '正在确认未检原因' },
        { value: '3005', label: '设备状态已变更未提交变更申请' },
        { value: '3006', label: '因保温层未拆除或流动式起重机无法实施检验' },
        { value: '3007', label: '备用锅炉或季节性使用锅炉' },
        { value: '3008', label: '其它' },
        { value: '4000', label: '因安全阀校验完成' },
    ],
    D_RISP_PER_RESULT2: [
        { value: '3001', label: '已检正在出具检验报告' },
        { value: '3002', label: '已约定检验时间' },
        { value: '3003', label: '已上报监察机构' },
        { value: '3004', label: '正在确认未检原因' },
        { value: '3005', label: '设备状态已变更未提交变更申请' },
        { value: '3006', label: '因保温层未拆除或流动式起重机无法实施检验' },
        { value: '3007', label: '备用锅炉或季节性使用锅炉' },
        { value: '3008', label: '其它' },
    ],
    D_RE_ISP: [
        { value: '是', label: '是' },
        { value: '否', label: '否' },
    ],
    JC_IMPORTCASE_ISPOPE: [
        { value: '1', label: '定期检验' },
        { value: '6', label: '外部检验' },
        { value: '2', label: '监督检验' },
        { value: '9', label: '其他检验' },
    ],
    OPEN_INVC_TYPE: [
        { value: '0', label: '未开票' },
        { value: '1', label: '单独开票' },
        { value: '2', label: '关联开票' },
    ],
    //其他业务受理状态
    D_OTHER_OPE_STATUS: [
        { value: '-1', label: '删除' },
        { value: '0', label: '未审核' },
        { value: '1', label: '审核通过未派工' },
        { value: '2', label: '审核完成' },
    ],
    Country: [
        { value: '1', label: '国内' },
        { value: '2', label: '国外' },
    ],
    //是否显示回退
    IF_REPGOBACK: [
        { value: '0', label: '是' },
        { value: '1', label: '否' },
        { value: '2', label: '显示明细' },
    ],
    IF_ITEMDESC: [
        { value: '0', label: '是' },
        { value: '1', label: '否' },
    ],
    //案例状态
    CASE_STA: [
        { value: '0', label: '编制' },
        { value: '1', label: '待审批' },
        { value: '2', label: '审批完成' },
        { value: '3', label: '不同意上报' },
    ],
    //是否经典案例
    IF_CLASS: [
        { value: '0', label: '是' },
        { value: '1', label: '否' },
    ],
    //是否重要事项已上报
    DefaultIFSep: [
        { value: '1', label: '已上报' },
        { value: '0', label: '未上报' },
    ],
    D_IF_REPORT_IMPCASE: [
        { value: '1', label: '已上报' },
        { value: '0', label: '未上报' },
    ],
    //问题
    D_PROBLEM_TYPE: [
        { value: '腐蚀', label: '腐蚀' },
        { value: '变形', label: '变形' },
        { value: '裂纹', label: '裂纹' },
        { value: '泄漏', label: '泄漏' },
        { value: '材质劣化', label: '材质劣化' },
        { value: '水垢', label: '水垢' },
        { value: '安全阀问题', label: '安全阀问题' },
        { value: '其他', label: '其他' },
    ],
    //处理
    D_DEAL_METHOD: [
        { value: '修理', label: '修理' },
        { value: '降压', label: '降压' },
        { value: '判废', label: '判废' },
        { value: '无', label: '无' },
    ],
    //整改单方式
    D_INFO_TYPE: [
        { value: '1', label: '开具检验意见通知书' },
        { value: '2', label: '开具工作联络单' },
        { value: '3', label: '未开具意见书(联络单' },
        { value: '4', label: '开具现场检验结果告知单' },
    ],
    //安全阀类型弹簧式,先导式,重锤式,静重式,杠杆式,脉冲式
    D_AQF_PA2: [
        { value: '弹簧式', label: '弹簧式' },
        { value: '先导式', label: '先导式' },
        { value: '重锤式', label: '重锤式' },
        { value: '静重式', label: '静重式' },
        { value: '杠杆式', label: '杠杆式' },
        { value: '脉冲式', label: '脉冲式' },
    ],
    //关键字段变更
    KEY_TABLE_NAME: [
        { value: 'TB_UNT_MGE', label: '单位台帐表' },
        { value: 'TB_UNT_DEPT', label: '单位部门明细表' },
        { value: 'TB_EQP_MGE', label: '设备台帐表' },
        { value: 'TB_TASK_MGE', label: '任务表' },
        { value: 'TB_ISP_MGE', label: '检验信息表' },
        { value: 'TB_INVC_MGE', label: '发票表' },
        { value: 'TB_CERT_MGE', label: '报告证发放表' },
        { value: 'TB_USER_INFO', label: '人员信息表' },
        { value: 'TB_BUSI_ACP_MGE', label: '综合业务受理表' },
    ],
    //工分查询
    GONGFEN_DATABASE: [
        { value: '1', label: '按月份查询' },
        { value: '2', label: '按月份+类别查询' },
        { value: '3', label: '按总分查询' },
    ],
    D_GONGFEN_TYPE: [
        { value: '报告编制', label: '报告编制' },
        { value: '责任工程师审核', label: '责任工程师审核' },
        { value: '复检', label: '复检' },
        { value: '取样', label: '取样' },
        { value: '检验业务', label: '检验业务' },
        { value: '限速器', label: '限速器' },
    ],
    //重要事项类型
    D_ISSUE_COD: [
        { value: 'A205', label: '检验不合格' },
        { value: 'A204', label: '超期未检' },
    ],
    //申请类型
    D_SDN_APPLY_TYPE: [
        { value: '1', label: '定检' },
        { value: '2', label: '监检' },
        { value: '3', label: '复检' },
        { value: '4', label: '监检协议' },
        { value: '5', label: '监检报检' },
    ],
    //监检未终结原因
    D_UNEND_TYPE: [
        { value: '1', label: '检验意见通知书或联络单' },
        { value: '2', label: '中止监检通知书' },
        { value: '3', label: '项目正常说明' },
    ],
    //发票重开处理内容
    INVC_OPETAG: [
        { value: '1', label: '转移旧票的所有关联信息至新发票' },
        { value: '2', label: '重开操作成功后作废旧发票' },
    ],
    //楼盘状态
    D_BUILD_STATE: [
        { value: '在用', label: '在用' },
        { value: '删除', label: '删除' },
    ],
    //监检未终结状态
    INSPSTOP_STATUS: [
        { value: '0', label: '删除' },
        { value: '301', label: '编制' },
        { value: '302', label: '审核' },
        { value: '303', label: '打印' },
        { value: '306', label: '终结' },
    ],
    //监检未终结状态
    INSPSTOP_STATUS2: [
        { value: '0', label: '删除' },
        { value: '301', label: '编制' },
        { value: '302', label: '已审核' },
    ],
    //质量管理-任务类型
    PLAN_TYPE: [
        { value: '1', label: '一级检查' },
        { value: '2', label: '二级检查' },
    ],
    //质量管理-检查类型
    QUAL_CHK_TYPE: [
        { value: '1', label: '报告记录检查' },
        { value: '2', label: '检验质量监督' },
        { value: '3', label: '检验质量验证' },
        { value: '4', label: '专项检查' },
    ],
    //质量管理-计划状态
    QUAL_CHK_STATUS: [
        { value: '0', label: '计划编制' },
        { value: '1', label: '提交' },
        { value: '2', label: '审批通过' },
        { value: '3', label: '作废' },
    ],
    //质量管理-任务状态
    QUAL_TASK_STATUS: [
        { value: '0', label: '待办' },
        { value: '1', label: '已办' },
        { value: '2', label: '已作废' },
    ],
    //质量管理-监督方式
    QUAL_VERI_METHOD: [
        { value: '0', label: '现场监督' },
        { value: '1', label: '验证' },
    ],
    //质量管理-问题类型
    QUAL_PROB_LEV: [
        { value: '0', label: '一般性' },
        { value: '1', label: '重大' },
        { value: '2', label: '其他' },
    ],
    //质量管理-监督验证结果
    QUAL_CHK_CONCLU: [
        { value: '0', label: '符合' },
        { value: '1', label: '一般性错误' },
        { value: '2', label: '重大错误' },
        { value: '3', label: '不适用' },
    ],
    //质量管理-任务类型
    QUAL_TASK_TYPE: [
        { value: '0', label: '计划任务' },
        { value: '1', label: '临时任务' },
    ],
    //质量管理-任务类型
    QUAL_STAT_TYPE: [
        { value: '1', label: '按检查类型' },
        { value: '2', label: '按设备种类' },
    ],
    //主动报检
    ACTJY_CURRNODE: [
        { value: '101', label: '报检受理' },
        { value: '102', label: '内部分配' },
        { value: '103', label: '办结' },
    ],
    //短信类型
    SMS_TYPE: [
        { value: '1', label: '检验通知-使用单位' },
        { value: '2', label: '检验通知-维保单位' },
        { value: '3', label: '缴款通知' },
    ],
    //仪器状态
    D_INS_STATE: [
        { value: '在用', label: '在用' },
        { value: '准用', label: '准用' },
        { value: '拟报废', label: '拟报废' },
        { value: '报废', label: '报废' },
        { value: '报停', label: '报停' },
    ],
    //仪器种类
    D_INS_TYPE: [
        { value: 'A类', label: 'A类' },
        { value: 'B类', label: 'B类' },
        { value: 'C类', label: 'C类' },
    ],
    //检定类型
    D_INS_CHECK_TYPE: [
        { value: '检定', label: '检定' },
        { value: '校准', label: '校准' },
        { value: '自校', label: '自校' },
        { value: '检查', label: '检查' },
    ],

    D_INSZJ_TASKCONCLU: [
        { value: '合格', label: '合格' },
        { value: '准用', label: '准用' },
        { value: '停用', label: '停用' },
        { value: '符合要求', label: '符合要求' },
        { value: '/', label: '/' },
    ],

    D_INSQJ_TASKCONCLU: [
        { value: '合格', label: '合格' },
        { value: '准用', label: '准用' },
        { value: '不合格', label: '不合格' },
        { value: '符合要求', label: '符合要求' },
    ],
    //账务归属
    D_FIN_ATT: [
        { value: '锅检', label: '锅检' },
        { value: '特检', label: '特检' },
    ],
    D_INS_MANG_LEVEL: [
        { value: '分院', label: '分院' },
        { value: '部门', label: '部门' },
        { value: '个人', label: '个人' },
    ],
    D_COST_TYPE: [
        { value: '锅检', label: '锅检' },
        { value: '特检', label: '特检' },
        { value: '特安', label: '特安' },
        { value: '劳安', label: '劳安' },
        { value: '其他', label: '其他' },
    ],
    D_CHECK_STA: [
        { value: '正常', label: '正常' },
        { value: '在用', label: '在用' },
        { value: '送检', label: '送检' },
    ],
    D_REPAIR_STA: [
        { value: '正常', label: '正常' },
        { value: '送修', label: '送修' },
    ],
    D_CHECK_CYCLE: [
        { value: '6', label: '6' },
        { value: '12', label: '12' },
        { value: '24', label: '24' },
        { value: '36', label: '36' },
        { value: '一次性', label: '一次性' },
        { value: '功能检查', label: '功能检查' },
    ],
    D_QJHC_CYCLE: [
        { value: '6', label: '6' },
        { value: '8', label: '8' },
    ],
    D_MANT_CYCLE: [
        { value: '3', label: '3' },
        { value: '6', label: '6' },
    ],
    D_INSUNT_TYPE: [
        { value: '1', label: '制造单位' },
        { value: '2', label: '代理单位' },
        { value: '3', label: '合格供应商' },
        { value: '4', label: '检定机构' },
    ],
    D_EVA_LEVEL: [
        { value: '一级', label: '一级' },
        { value: '二级', label: '二级' },
        { value: '三级', label: '三级' },
        { value: '四级', label: '四级' },
        { value: '五级', label: '五级' },
    ],
    D_FORCE_CHECK_SORT: [
        { value: '非强检', label: '非强检' },
        { value: '强检', label: '强检' },
        { value: '/', label: '/' },
    ],
    D_INSTASK_TYPE: [
        { value: '1', label: '周期检定' },
        { value: '2', label: '期间核查' },
        { value: '3', label: '维保' },
    ],
    D_ITEM_A: [
        { value: '1分', label: '1分' },
        { value: '2分', label: '2分' },
        { value: '3分', label: '3分' },
        { value: '4分', label: '4分' },
        { value: '5分', label: '5分' },
    ],
    D_ITEM_F: [
        { value: 'A', label: 'A' },
        { value: 'B', label: 'B' },
        { value: 'C', label: 'C' },
        { value: 'D', label: 'D' },
    ],
    D_ITEM_G: [
        { value: '1', label: '1' },
        { value: '2', label: '2' },
        { value: '3', label: '3' },
    ],
    D_ITEM_H: [
        { value: '1', label: '1' },
        { value: '2', label: '2' },
        { value: '3', label: '3' },
        { value: '4', label: '4' },
        { value: '5', label: '5' },
        { value: '6', label: '6' },
    ],
    D_INS_TASKSTA: [
        { value: '0', label: '未完成' },
        { value: '1', label: '已送检' },
        { value: '2', label: '已完成' },
        { value: '3', label: '作废' },
    ],
    //计划状态
    D_StockPlan_sta: [
        { value: '1001', label: '编制' },
        { value: '1002', label: '审核' },
        { value: '1003', label: '审批' },
        { value: '1004', label: '评审' },
        { value: '-1', label: '作废' },
    ],
    //信工分查询方式
    D_GONGFENQ: [
        { value: '1', label: '检验时间' },
        { value: '2', label: '检验+路途时间' },
    ],
    BJ_STA: [
        { value: '-2', label: '未报检' },
        { value: '0', label: '申请' },
        { value: '2', label: '已受理' },
        { value: '1', label: '已办结' },
    ], //数据库中设置的空为未报检
    BJ_STA2: [
        { value: '申请', label: '申请' },
        { value: '已受理', label: '已受理' },
        { value: '已办结', label: '已办结' },
    ],
    //缴款方式
    PAY_WAY: [
        { value: '1', label: '银行转账' },
        { value: '2', label: '现金收取' },
        { value: '3', label: 'POS机' },
        { value: '4', label: '支付宝' },
        { value: '5', label: '微信' },
    ],
    //报检说明
    ACP_DES: [
        { value: '1', label: '使用单位同意检验，申请报检' },
        { value: '0', label: '使用单位单位因其它情况，无法报检' },
    ],

    UNT_TYPE_FORCHG: [
        { value: 'USE_UNT_ID', label: '使用单位' },
        { value: 'MANT_UNT_ID', label: '维保单位' },
        { value: 'ALT_UNT_ID', label: '改造单位' },
        { value: 'MAKE_UNT_ID', label: '制造单位' },
        { value: 'INST_UNT_ID', label: '安装单位' },
        { value: 'OVH_UNT_ID', label: '维修单位' },
    ],
    D_FILE_STATUS: [
        { value: '0', label: '未提交' },
        { value: '1', label: '已提交' },
        { value: '2', label: '已归档' },
    ],
    D_PACT_TYPE: [
        { value: '0', label: '政府采购合同' },
        { value: '1', label: '自行采购合同' },
        { value: '2', label: '劳动/劳务合同' },
        { value: '3', label: '特殊合同' },
    ],
    D_BUDGET_ORIGIN: [
        { value: '0', label: '锅检' },
        { value: '1', label: '特检' },
    ],
    D_IF_NOREG_LEGAR: [
        { value: '0', label: '否' },
        { value: '1', label: '是' },
    ],
    //是否开票完结
    IFTICKET: [
        { value: '0', label: '未开' },
        { value: '1', label: '已开' },
        { value: '2', label: '作废' },
    ],
    //是否回款完结
    IFPAY: [
        { value: '0', label: '未回款' },
        { value: '1', label: '已回款' },
        { value: '2', label: '作废' },
    ],
    //审核意见
    JIAJI_REPORT_CASE: [
        { value: '1', label: '省市重点工程' },
        { value: '2', label: '政府民生工程' },
        { value: '3', label: '重大活动保障' },
        { value: '99', label: '其他' },
    ],
    //费用类型
    FEE_MOD_TYPE: [
        { value: '1', label: '基础检验收费' },
        { value: '2', label: '附加检验收费' },
        { value: '8', label: '基础检验加收费' },
        { value: '9', label: '其他收费' },
    ],
    //加急审批状态
    APPR_STATUS: [
        { value: '0', label: '申请中' },
        { value: '1', label: '审批通过' },
        { value: '2', label: '审批不通过' },
    ],
    //加急审核结果
    CHK_OPTIONS: [
        { value: '0', label: '未审核' },
        { value: '1', label: '审核通过' },
        { value: '2', label: '审核不通过' },
    ],
    //催检状态
    PROM_STATE: [
        { value: '0', label: '未催检' },
        { value: '1', label: '已催检' },
    ],
    //预约状态
    ORD_STATE: [
        { value: '0', label: '预约未成功' },
        { value: '1', label: '预约成功' },
    ],
    //预约不成功原因
    ORD_FAIL: [
        { value: '0', label: '已联系未预约成功' },
        { value: '1', label: '电话无法接通' },
    ],
    //报告领取方式
    REC_TYPE: [
        { value: '1', label: '窗口领取' },
        { value: '2', label: '邮寄' },
    ],
    //缴款方式
    PAY_WAY1: [
        { value: '1', label: '银行转账' },
        { value: '2', label: '现金收取' },
        { value: '4', label: '据实核算' },
    ],

    //发票关联检验信息
    ASS_STA: [
        { value: '已关联', label: '已关联' },
        { value: '未关联', label: '未关联' },
    ],

    //缴款人类型
    PAYMEN_TYPE: [
        { value: '2', label: '单位' },
        { value: '1', label: '个人' },
    ],
};

export default Dictionary;
