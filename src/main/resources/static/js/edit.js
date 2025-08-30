// 햄버거 메뉴
const menuToggle = document.getElementById("menuToggle");
const sideNav = document.getElementById("sideNav");

menuToggle.addEventListener("click", () => {
    sideNav.classList.toggle("active");
});

document.addEventListener("click", (e) => {
    if (!sideNav.contains(e.target) && !menuToggle.contains(e.target)) {
        sideNav.classList.remove("active");
    }
});

// 로그인 모달창
document.querySelectorAll('.btn-login').forEach(btn => {
    btn.addEventListener('click', () => {
        document.getElementById('loginModal').style.display = 'block';
    });
});

// 회원가입 모달창
document.querySelectorAll('.btn-signup').forEach(btn => {
    btn.addEventListener('click', () => {
        document.getElementById('signupModal').style.display = 'block';
    });
});

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
const form = document.querySelector('#register');
const input = document.querySelector('#birthday');

form.addEventListener('submit', (e) => {
    if (!/^\d{8}$/.test(input.value)) {
        e.preventDefault();
        alert("생년월일 8자리 숫자를 입력해주세요. (ex:19001010)");
    }
});

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

// TinyMCE 글쓰기
//    tinymce.init({
//        selector: '#editor',
//        language: 'ko_KR',
//        height: 500,
//        menubar: true,
//        plugins: [
//            'print preview paste importcss searchreplace autolink autosave save',
//            'directionality code visualblocks visualchars fullscreen image link',
//            'media template codesample table charmap hr pagebreak nonbreaking',
//            'anchor toc insertdatetime advlist lists wordcount help charmap quickbars emoticons'
//        ],
//        toolbar: 'undo redo | blocks | bold italic underline strikethrough | forecolor backcolor |' +
//            'alignleft aligncenter alignright alignjustify | outdent indent | numlist bullist |' +
//            'image media link table hr charmap emoticons | removeformat | code fullscreen preview',
//        menubar: 'file edit view insert format tools table help',
//        content_style: 'body { font-family:Quicksand,sans-serif; font-size:14px }',
//        mobile: {
//          menubar: true  // 모바일에서도 메뉴바 표시
//        },
//        branding: false  // Powered by Tiny 로고 숨김
//    });




// 첨부파일 선택시 파일명 보여주기
const fileInput = document.getElementById('attachment');
const fileListDiv = document.getElementById('file-list');

fileInput.addEventListener('change', function() {
    fileListDiv.innerHTML = ''; // 기존 파일 목록 초기화

    const files = fileInput.files;
    if (files.length > 0) {
        const ul = document.createElement('ul');
        for (let i = 0; i < files.length; i++) {
            const li = document.createElement('li');
            li.textContent = files[i].name;  // 파일명+확장자
            ul.appendChild(li);
        }
        fileListDiv.appendChild(ul);
    } else {
        fileListDiv.textContent = '선택된 파일이 없습니다.';
    }
});


// 첨부파일 확장자 제한
const allowedExtensions = [
    'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx',
    'hwp', 'hwpx', 'txt', 'csv', 'md', 'log', 'json', 'xml',
    'jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp',
    'zip', '7z', 'rar', 'tar', 'gz', 'tgz',
    'mp3', 'wav', 'mp4', 'mov', 'ogg', 'avi', 'webm'
];

document.getElementById('attachment').addEventListener('change', function () {
    const files = this.files;
    const warningDiv = document.getElementById('file-warning');
    let invalidFiles = [];

    for (let i = 0; i < files.length; i++) {
        const ext = files[i].name.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(ext)) {
            invalidFiles.push(files[i].name);
        }
    }

    if (invalidFiles.length > 0) {
        warningDiv.style.display = 'block';
        warningDiv.innerText = `❌ 허용되지 않은 파일 형식입니다: ${invalidFiles.join(', ')}`;
    } else {
        warningDiv.style.display = 'none';
        warningDiv.innerText = '';
    }
});


// ✅ Toast UI 에디터 초기화
const editor = new toastui.Editor({
    el: document.querySelector('#editor'),
    height: '500px',
    initialEditType: 'wysiwyg',
    previewStyle: 'vertical',
    language: 'ko-KR',
    toolbarItems: [
        ['heading', 'bold', 'italic', 'strike'],
        ['hr', 'quote'],
        ['ul', 'ol', 'task'],
        ['table', 'link', 'image'],
        [
            {
                name: 'uploadVideo',
                tooltip: '동영상 업로드',
                className: 'toastui-editor-icon-video',
            }
        ]
    ],
    hooks: {
        addImageBlobHook: async (blob, callback) => {
            const formData = new FormData();
            formData.append('image', blob);
            const response = await fetch('/upload_image', {
                method: 'POST',
                body: formData
            });
            const data = await response.json();
            callback(data.url, '업로드된 이미지');
        }
    },
    customHTMLSanitizer: (html) => {
        return html
            .replace(/<\/?script[^>]*>/g, '')  // 기존 script 제거
            .replace(/<\/?iframe[^>]*>/g, ''); // 추가: iframe도 제거
    }
});


// ✅ 폼 제출 시 내용 저장
//    document.querySelector('.write-form').addEventListener('submit', function (e) {
//      const content = editor.getHTML();
//      document.getElementById('contentHidden').value = content;
//    });


// ✅ 전역 변수
let videoUploadQueue = [];
let uploadedVideoMap = {};  // 마커텍스트 → hex 변환 매핑용

// ✅ 동영상 업로드 버튼 클릭 이벤트
// 에디터에는 원본 파일명 기반 마커 텍스트만 삽입함
// 예: [ 동영상 : original_filename.mp4 ]
document.addEventListener('click', function (e) {
    const target = e.target.closest('.toastui-editor-icon-video');
    if (!target) return;

    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'video/*';
    input.multiple = true;
    input.style.display = 'none';

    input.onchange = () => {
        const files = Array.from(input.files);
        videoUploadQueue.push(...files);

        files.forEach(file => {
            console.log("[DEBUG] file 전체 객체", file);
            console.log("[DEBUG] 원본 파일명: ", file.name);
            const markerText = `[ 동영상 : ${file.name} ]`;
            if (typeof editor.insertText === 'function') {
                editor.insertText(markerText + '\n');
            } else if (typeof editor.insertHTML === 'function') {
                editor.insertHTML(`<p>${markerText}</p>`);
            }
        });
    };

    document.body.appendChild(input);
    input.click();
    document.body.removeChild(input);
});

// ✅ 게시글 작성 이벤트
const writeForm = document.querySelector('.write-form');
writeForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    const modal = document.getElementById('uploadModal');
    modal.style.display = 'block';
    document.body.style.pointerEvents = 'none';
    document.getElementById('uploadStatus').innerText = '동영상 업로드 중...';

    let uploadedCount = 0;
    let editorHtml = editor.getHTML();

    try {
        for (let i = 0; i < videoUploadQueue.length; i++) {
            const file = videoUploadQueue[i];

            await new Promise((resolve, reject) => {
                const formData = new FormData();
                formData.append('video', file);

                const xhr = new XMLHttpRequest();
                xhr.open('POST', '/upload_video');

                // ✅ 진행률 반영
                xhr.upload.onprogress = function (e) {
                    if (e.lengthComputable) {
                        const percent = Math.round((e.loaded / e.total) * 100);
                        const totalPercent = Math.round(((i + percent / 100) / videoUploadQueue.length) * 100);
                        document.getElementById('uploadProgress').style.width = totalPercent + '%';
                    }
                };

                xhr.onload = function () {
                    uploadedCount++;
                    try {
                        const result = JSON.parse(xhr.responseText);
                        if (result.success) {
                            const originalFilename = file.name;
                            const hexFilename = result.stored_name;
                            const thumbnailUrl = `/static/uploads/${hexFilename.replace(/\.[^/.]+$/, '.jpg')}`;

                            uploadedVideoMap[originalFilename] = {
                                stored: hexFilename,
                                thumb: thumbnailUrl
                            };

                            resolve();
                        } else {
                            reject('업로드 실패: ' + result.message);
                        }
                    } catch (e) {
                        reject('응답 파싱 오류: ' + e.message);
                    }
                };

                xhr.onerror = () => reject('서버 오류');
                xhr.send(formData);
            });
        }

        // ✅ 마커텍스트 일괄 변환
        const markerRegex = /\[ *동영상 *: *([^\]]+\.(mp4|mov|webm|ogg|avi)) *\]/g;
        editorHtml = editorHtml.replace(markerRegex, (match, filename) => {
            const info = uploadedVideoMap[filename.trim()];
            if (!info) return match;

            const ext = info.stored.split('.').pop();
            return `<video controls style="max-width: 100%;" poster="${info.thumb}">
  <source src="/static/uploads/${info.stored}" type="video/${ext}">
  브라우저가 동영상을 지원하지 않습니다.
</video>`;
        });

        document.getElementById('uploadProgress').style.width = '100%';
        document.getElementById('uploadStatus').innerText = '게시글 등록 완료!';
        document.getElementById('contentHidden').value = editorHtml;

        setTimeout(() => {
            modal.style.display = 'none';
            document.body.style.pointerEvents = 'auto';
            writeForm.submit();
        }, 1000);
    } catch (err) {
        alert(err);
        modal.style.display = 'none';
        document.body.style.pointerEvents = 'auto';
    }
});