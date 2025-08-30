// 햄버거 메뉴
const menuToggle = document.getElementById("menuToggle");
const sideNav = document.getElementById("sideNav");

if (menuToggle && sideNav) {
    menuToggle.addEventListener("click", () => {
        sideNav.classList.toggle("active");
    });

    document.addEventListener("click", (e) => {
        if (!sideNav.contains(e.target) && !menuToggle.contains(e.target)) {
            sideNav.classList.remove("active");
        }
    });
}

// 로그인 모달창
// document.querySelectorAll('.btn-login').forEach(btn => {
//     btn.addEventListener('click', () => {
//         document.getElementById('loginModal').style.display = 'block';
//     });
// });

// 회원가입 모달창
// document.querySelectorAll('.btn-signup').forEach(btn => {
//     btn.addEventListener('click', () => {
//         document.getElementById('signupModal').style.display = 'block';
//     });
// });

// 모달창 닫기
function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

window.addEventListener('click', (event) => {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
});

// 회원가입-생년월일 8자리 입력 유효성검사
// const form = document.querySelector('#register');
// const input = document.querySelector('#birthday');
//
// form.addEventListener('submit', (e) => {
//     if (!/^\d{8}$/.test(input.value)) {
//         e.preventDefault();
//         alert("생년월일 8자리 숫자를 입력해주세요. (ex:19001010)");
//     }
// });

// 이용약관 모달창
document.getElementById('openTerms').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('termsModal').style.display = 'block';
});

// 개인정보처리방침 모달창
document.getElementById('openPrivate').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('privateModal').style.display = 'block';
});

// 개인정보 수집 및 이용 동의 모달창
// document.getElementById('openAgreement').addEventListener('click', function(e) {
//     e.preventDefault();
//     document.getElementById('agreementModal').style.display = 'block';
// });

// Toast UI 에디터 실제 내용 전송
// const writeForm = document.querySelector('.write-form');
// writeForm.addEventListener('submit', function(e) {
//     const content = editor.getHTML();  // 에디터에서 HTML 꺼냄
//     document.getElementById('contentHidden').value = content;  // 숨은 input에 넣기
// });

// Toast UI 글쓰기
// const editor = new toastui.Editor({
//     el: document.querySelector('#editor'),
//     initialValue: `{{ post.content | safe }}`,  // 서버에서 렌더링된 HTML 콘텐츠
//     height: '500px',
//     initialEditType: 'wysiwyg',
//     previewStyle: 'vertical',
//     language: 'ko-KR',
// });

// 첨부파일 선택시 파일명 보여주기
// const fileInput = document.getElementById('attachment');
// const fileListDiv = document.getElementById('file-list');
//
// fileInput.addEventListener('change', function() {
//     fileListDiv.innerHTML = ''; // 기존 파일 목록 초기화
//
//     const files = fileInput.files;
//     if (files.length > 0) {
//         const ul = document.createElement('ul');
//         for (let i = 0; i < files.length; i++) {
//             const li = document.createElement('li');
//             li.textContent = files[i].name;  // 파일명+확장자
//             ul.appendChild(li);
//         }
//         fileListDiv.appendChild(ul);
//     } else {
//         fileListDiv.textContent = '선택된 파일이 없습니다.';
//     }
// });