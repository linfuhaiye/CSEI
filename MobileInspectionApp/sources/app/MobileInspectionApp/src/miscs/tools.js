/**
 * 创建指定位数随机字符串（默认16位）
 *
 * @param {*} bit 位数
 */
export const createNonceStr = (bit) => {
    let chars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
    let stringLength = bit || 16;
    let nums = '';
    for (let i = 0; i < stringLength; i++) {
        let id = parseInt(Math.random() * 61);
        nums += chars[id];
    }
    return nums;
};

export const fieldType = [
    { type: 'unknow', value: 0 },
    { type: 'pushButton', value: 1 },
    { type: 'checkbox', value: 2 },
    { type: 'radio', value: 3 },
    { type: 'dropdown', value: 4 },
    { type: 'listBox', value: 5 },
    { type: 'labelInput', value: 6 },
    { type: 'signature', value: 7 },
];

export const fieldFlag = [
    { type: 'unknow', value: 0 },
];
