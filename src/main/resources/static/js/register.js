
// 햄버거 메뉴 (없는 페이지에서도 에러 안 나게)
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
//     const form = document.querySelector('#register-form');
//     const input = document.querySelector('#birthday');
//
//     form.addEventListener('submit', (e) => {
//     if (!/^\d{8}$/.test(input.value)) {
//     e.preventDefault();
//     alert("생년월일 8자리 숫자를 입력해주세요. (ex:19001010)");
// }
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
document.getElementById('openAgreement').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('agreementModal').style.display = 'block';
});


// 중복확인 및 비밀번호확인 공통함수
// let isDuplicateChecked = false;
// let isPasswordMatched = false;
//
//     function updateRegisterButtonState() {
//     if (isDuplicateChecked && isPasswordMatched) {
//     registerBtn.disabled = false;
// } else {
//     registerBtn.disabled = true;
// }
// }

// userid 중복 확인
//     const useridInput = document.getElementById('userid');
//     const registerBtn = document.getElementById('register-btn');
//     const checkDuplicateBtn = document.getElementById('check-duplicate-btn');
//
//     checkDuplicateBtn.addEventListener('click', function() {
//     const userid = useridInput.value.trim();
//     if (!userid) {
//     alert("아이디를 입력해 주세요.");
//     return;
// }
//
//     fetch('/check-duplicate', {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     body: JSON.stringify({ userid: userid })
// })
//     .then(res => res.json())
//     .then(data => {
//     if (data.isDuplicate) {
//     alert("이미 사용 중인 아이디입니다.");
//     isDuplicateChecked = false;
//     updateRegisterButtonState();
// } else {
//     alert("사용 가능한 아이디입니다!");
//     isDuplicateChecked = true;
//     updateRegisterButtonState();
// }
// })
//     .catch(error => {
//     console.error('중복확인 에러:', error);
//     alert("중복확인 실패! 다시 시도해주세요.");
//     isDuplicateChecked = false;
//     updateRegisterButtonState();
// });
// });
//
//     useridInput.addEventListener('input', function() {
//     isDuplicateChecked = false;
//     registerBtn.disabled = true;
//     document.getElementById('userid-warning').style.display = 'block';
// });




// 비밀번호 확인 일치
//     const passwordInput = document.getElementById('password');
//     const confirmPasswordInput = document.getElementById('confirm-password');
//     const passwordWarning = document.getElementById('password-warning');
//
//     // 비밀번호 일치 검사 함수
//     function checkPasswordMatch() {
//     const password = passwordInput.value.trim();
//     const confirmPassword = confirmPasswordInput.value.trim();
//
//     if (password !== "" && confirmPassword !== "") {
//     if (password !== confirmPassword) {
//     passwordWarning.style.display = 'block';
//     isPasswordMatched = false;
// } else {
//     passwordWarning.style.display = 'none';
//     isPasswordMatched = true;
// }
// } else {
//     passwordWarning.style.display = 'none';
//     isPasswordMatched = false;
// }
//
//     // 매번 상태에 따라 버튼 제어
//     updateRegisterButtonState();
// }
//
//
//     // 비밀번호 입력창, 비밀번호 확인창 모두에 이벤트 걸기
//     passwordInput.addEventListener('input', checkPasswordMatch);
//     confirmPasswordInput.addEventListener('input', checkPasswordMatch);



document.addEventListener("DOMContentLoaded", () => {
    const pwd    = document.getElementById("password");
    const confirm= document.getElementById("confirm-password");
    const agree  = document.getElementById("privacyAgree");
    const btn    = document.getElementById("register-btn");
    const warn   = document.getElementById("password-warning");

    function validateUIOnly() {
        const p = pwd ? pwd.value : "";
        const c = confirm ? confirm.value : "";

        // 경고문구만 표시, 버튼은 막지 않는다
        if (warn) {
            warn.style.display = (c && p && c !== p) ? "block" : "none";
        }
    }

    [pwd, confirm, agree].forEach(el => el && el.addEventListener("input", validateUIOnly));
    validateUIOnly();
});



// 회원가입
//     document.getElementById('register-form').addEventListener('submit', function(e) {
//     e.preventDefault();
//
//     const formData = new FormData(this);
//
//     fetch('/register', {
//     method: 'POST',
//     body: formData
// })
//     .then(res => res.json())
//     .then(data => {
//     if (data.error) {
//     alert(data.error);
// } else {
//     alert(data.message);
//     window.location.href = '/';
// }
// })
//     .catch(err => {
//     console.error('회원가입 에러:', err);
//     alert("회원가입 실패!");
// });
// });
//
//
//
//     // 로그인 처리
//     const loginBtn = document.getElementById('login-btn');
//
//     loginBtn.addEventListener('click', function(event) {
//     event.preventDefault();  // 폼 기본 제출 막기
//
//     const userid = document.getElementById('userid2').value.trim();
//     const password = document.getElementById('password2').value.trim();
//
//     if (!userid || !password) {
//     alert('아이디와 비밀번호를 모두 입력하세요.');
//     return;
// }
//
//     fetch('/login', {
//     method: 'POST',
//     headers: {
//     'Content-Type': 'application/json'
// },
//     body: JSON.stringify({ userid: userid, password: password })
// })
//     .then(res => res.json())
//     .then(data => {
//     if (data.success) {
//     alert('로그인 성공!  즐거운 시간 되세요.');
//     location.href = '/home';  // 로그인 성공 시 이동할 경로
// } else {
//     alert(data.error || '로그인 실패!');
// }
// })
//     .catch(error => {
//     console.error('로그인 에러:', error);
//     alert('서버 오류! 잠시 후 다시 시도하세요.');
// });
// });
//
//
//     // PWA (Progressive Web App) 웹앱 서비스워커 등록 스크립트
//     if ('serviceWorker' in navigator) {
//     navigator.serviceWorker.register('/static/js/service-worker.js')
//         .then(reg => console.log("✅ Service Worker 등록 성공!", reg))
//         .catch(err => console.log("❌ Service Worker 등록 실패:", err));
// }