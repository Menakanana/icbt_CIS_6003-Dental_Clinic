<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Global Toast Container -->
<div id="globalToastContainer" class="global-toast-container"></div>

<!-- Global Custom Confirmation & Prompt Modal -->
<div id="globalCustomModal" class="custom-modal-overlay" style="display: none;" onclick="if(event.target === this) closeCustomModal();">
    <div class="custom-modal-box" onclick="event.stopPropagation();">
        <div class="custom-modal-header">
            <h3 id="globalCustomModalTitle">Confirmation</h3>
            <button type="button" class="custom-modal-close" onclick="closeCustomModal()">&times;</button>
        </div>
        <div class="custom-modal-body">
            <p id="globalCustomModalMessage" style="margin: 0;"></p>
            <div id="globalCustomModalInputWrapper" style="display: none; margin-top: 1rem;">
                <input type="text" id="globalCustomModalInput" class="form-control" placeholder="Enter value..." style="width: 100%; padding: 0.6rem; border-radius: 6px; border: 1px solid #CBD5E1; font-size: 0.9rem; box-sizing: border-box;">
            </div>
        </div>
        <div class="custom-modal-footer">
            <button type="button" id="globalCustomModalCancelBtn" class="btn-cancel" onclick="closeCustomModal()">Cancel</button>
            <button type="button" id="globalCustomModalConfirmBtn" class="btn-confirm">Confirm</button>
        </div>
    </div>
</div>

<script>
    window.showToast = function(message, type = 'info', duration = 4000) {
        let container = document.getElementById('globalToastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'globalToastContainer';
            container.className = 'global-toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = 'global-toast toast-' + type;
        toast.innerHTML = '<span>' + message + '</span>';
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 350);
        }, duration);
    };

    window.showCustomConfirm = function(title, message, onConfirmCallback, isDanger = false) {
        const modal = document.getElementById('globalCustomModal');
        const titleEl = document.getElementById('globalCustomModalTitle');
        const msgEl = document.getElementById('globalCustomModalMessage');
        const inputWrapper = document.getElementById('globalCustomModalInputWrapper');
        const confirmBtn = document.getElementById('globalCustomModalConfirmBtn');

        if (!modal) return;

        titleEl.innerText = title || 'Confirmation';
        msgEl.innerText = message || 'Are you sure?';
        inputWrapper.style.display = 'none';

        confirmBtn.className = 'btn-confirm' + (isDanger ? ' btn-danger' : '');
        confirmBtn.innerText = 'Confirm';

        confirmBtn.onclick = function() {
            closeCustomModal();
            if (typeof onConfirmCallback === 'function') {
                onConfirmCallback();
            }
        };

        modal.style.display = 'flex';
    };

    window.showCustomPrompt = function(title, message, defaultValue, onSubmitCallback) {
        const modal = document.getElementById('globalCustomModal');
        const titleEl = document.getElementById('globalCustomModalTitle');
        const msgEl = document.getElementById('globalCustomModalMessage');
        const inputWrapper = document.getElementById('globalCustomModalInputWrapper');
        const inputEl = document.getElementById('globalCustomModalInput');
        const confirmBtn = document.getElementById('globalCustomModalConfirmBtn');

        if (!modal) return;

        titleEl.innerText = title || 'Input Required';
        msgEl.innerText = message || 'Please enter details:';
        inputWrapper.style.display = 'block';
        inputEl.value = defaultValue || '';

        confirmBtn.className = 'btn-confirm';
        confirmBtn.innerText = 'Submit';

        const submitHandler = function() {
            const val = inputEl.value;
            closeCustomModal();
            if (typeof onSubmitCallback === 'function') {
                onSubmitCallback(val);
            }
        };

        confirmBtn.onclick = submitHandler;
        inputEl.onkeydown = function(e) {
            if (e.key === 'Enter') {
                submitHandler();
            }
        };

        modal.style.display = 'flex';
        setTimeout(() => inputEl.focus(), 100);
    };

    window.closeCustomModal = function() {
        const modal = document.getElementById('globalCustomModal');
        if (modal) modal.style.display = 'none';
    };
</script>
