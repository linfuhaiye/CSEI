/**
 * 睡眠
 *
 * @export
 * @param {*} millisecond 毫秒
 */
export async function sleep(millisecond) {
    await (() => new Promise((resolve) => setTimeout(resolve, millisecond)))();
}
