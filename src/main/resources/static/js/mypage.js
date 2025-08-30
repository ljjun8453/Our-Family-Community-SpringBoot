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



// 마이페이지 비밀번호 변경시 유효성 검사
document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
    const currentPassword = document.getElementById('current_password');
    const newPassword = document.getElementById('new_password');
    const confirmPassword = document.getElementById('confirm_password');

    const errorCurrent = document.getElementById('error_current');
    const errorConfirm = document.getElementById('error_confirm');

    // ✅ 현재 비밀번호 서버 확인
    currentPassword.addEventListener('input', () => {
        const value = currentPassword.value.trim();

        if (value === '') {
            errorCurrent.textContent = '';
            return;
        }

        fetch('/check_current_password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRFToken': csrfToken
            },
            body: JSON.stringify({ current_password: value })
        })
            .then(res => res.json())
            .then(data => {
                errorCurrent.textContent = data.valid ? '' : '현재 비밀번호가 일치하지 않습니다.';
            });
    });

    // ✅ 새 비밀번호와 새 비밀번호 확인 비교
    function validatePasswords() {
        if (newPassword.value === '' || confirmPassword.value === '') {
            errorConfirm.textContent = '';
            return;
        }

        if (newPassword.value !== confirmPassword.value) {
            errorConfirm.textContent = '비밀번호가 일치하지 않습니다.';
        } else {
            errorConfirm.textContent = '';
        }
    }

    newPassword.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);
});