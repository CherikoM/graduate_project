/**
 * 清除粘贴样式
 * @param e 
 */
export default (e: any) => {
  e.preventDefault();
  let text;
  const clp = (e.originalEvent || e).clipboardData;
  if (clp === undefined || clp === null) {
    //@ts-ignore
    text = window.clipboardData.getData("text") || "";
    if (text !== "") {
      if (window.getSelection) {
        const newNode = document.createElement("span");
        newNode.innerHTML = text;
        window.getSelection()?.getRangeAt(0).insertNode(newNode);
      } else {
        //@ts-ignore
        document.selection.createRange().pasteHTML(text);
      }
    }
  }
  else {
    text = clp.getData('text/plain') || "";
    if (text !== "") {
      document.execCommand('insertText', false, text);
      navigator.clipboard.writeText(text)
    }
  }
}