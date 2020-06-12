function reset() {
    window.onbeforeunload = function () {
      window.scrollTo(0, 0);
    }
    localStorage.setItem("processing", "false");
    updateDate();
}

function updateDate() {
    getDate(document.getElementById("date"))
    getDate(document.getElementById("end"))
    getDate(document.getElementById("start"))
}

function getDate(date) {
    if (date !== null) {
        let getDate = function() {
            let today = new Date();
            let dd = today.getDate();
            let mm = today.getMonth() + 1;
            let yyyy = today.getFullYear();
            if(dd<10) dd='0'+dd;
            if(mm<10) mm='0'+mm;
            return (yyyy+"-"+mm+"-"+dd);
        };
        date.value = getDate();
    }
}
/*----------------------------------------------------- forms ---------------------------------------- */
const createForm = document.getElementById("createForm");

// When the user clicks anywhere outside of any pop up windows, close it
window.onclick = function (event) {
    if (localStorage.getItem("processing") != "true"
        && event.target == createForm) {
        createForm.style.display = "none";
    }
}
// When the user clicks the button, open the form
function openForm() {
    createForm.style.display = "flex";
}
/*-------------------- editable form -------------------- */
(function (window, ElementPrototype, ArrayPrototype, polyfill) {
    function NodeList() { [polyfill] }
    NodeList.prototype.length = ArrayPrototype.length;

    ElementPrototype.matchesSelector =
        ElementPrototype.matchesSelector ||
        ElementPrototype.webkitMatchesSelector  ||
        function matchesSelector(selector) {
            return ArrayPrototype.indexOf.call(this.parentNode.querySelectorAll(selector), this) > -1;
        };

    ElementPrototype.ancestorQuerySelectorAll = ElementPrototype.ancestorQuerySelectorAll  ||
        function ancestorQuerySelectorAll(selector) {
            for (var cite = this, newNodeList = new NodeList; cite = cite.parentElement;) {
                if (cite.matchesSelector(selector)) ArrayPrototype.push.call(newNodeList, cite);
            }

            return newNodeList;
        };

    ElementPrototype.ancestorQuerySelector = ElementPrototype.ancestorQuerySelector  ||
        function ancestorQuerySelector(selector) {
            return this.ancestorQuerySelectorAll(selector)[0] || null;
        };
})(this, Element.prototype, Array.prototype);
let item;
function generateTableRow() {
    item += 1;
    const emptyColumn = document.createElement('tr');
    emptyColumn.innerHTML = '' +
        '<td><a class="cut">-</a>' +
        '<span class="iNumber">'+item+'</span></td>' +
        '<td><input type="number" class="productId" name="productId" min="0" max="9999"></td>' +
        '<td><input type="number" class="price" name="price" min="0" max="9999" placeholder="$0.00"></td>' +
        '<td><input type="number" class="quantity" name="quantity" min="0" max="9999" placeholder="0"></td>';
    if (item % 2 != 0) {
        emptyColumn.style.backgroundColor = 'rgb(242, 242, 242)';
    }
    return emptyColumn;
}

function onContentLoad() {
    item = 0;
    function onClick(e) {
        let element = e.target.querySelector('[contentEditable]'), row;
        element && e.target != document.documentElement && e.target != document.body && element.focus();
        if (e.target.matchesSelector('.add')) {
            document.querySelector('table.details tbody').appendChild(generateTableRow());
        }
        if (e.target.className == 'cut') {
            row = e.target.ancestorQuerySelector('tr');
            row.parentNode.removeChild(row);
            let itemNumbers = document.getElementsByClassName("iNumber");
            let count;
            for (count = 0; count < itemNumbers.length; count++) {
                itemNumbers[count].innerHTML = count + 1;
                let row = itemNumbers[count].parentElement.parentElement;
                if ((count + 1) % 2 != 0) {
                    row.style.backgroundColor = 'rgb(242, 242, 242)';
                } else {
                    row.style.backgroundColor = 'white';
                }
            }
            item = count;
        }
    }
    if (window.addEventListener) {
        document.addEventListener('click', onClick);
    }
}

window.addEventListener && document.addEventListener('DOMContentLoaded', onContentLoad);

/*------------text area auto resize---------------------*/
const tx = document.getElementsByTagName('textarea');
for (let i = 0; i < tx.length; i++) {
    tx[i].addEventListener("input", OnInput, false);
}

function OnInput() {
    if (this.scrollHeight <= 100) {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
        this.style.overflow = "hidden";
    } else {
        this.style.overflow = "scroll";
    }
}
/*---------------------------------------------to top page button----------------------------------- */
//back to top btn
const upBtn = document.getElementById("upBtn");
// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function () {showUpBtn() };
function showUpBtn() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        upBtn.style.display = "block";
    } else {
        upBtn.style.display = "none";
    }
}
// When the user clicks on the button, scroll to the top of the document
function toTop() {
    window.scrollTo(0, 0);
}
