// Custom JavaScript for File Generator BVV

// Document Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('File Generator BVV - Application loaded');

    // Initialize tooltips
    initTooltips();

    // Auto-hide alerts after 5 seconds
    autoHideAlerts();

    // Add fade-in animation to cards
    addFadeInAnimation();
});

/**
 * Initialize Bootstrap tooltips
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Auto-hide alerts after 5 seconds
 */
function autoHideAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

/**
 * Add fade-in animation to cards
 */
function addFadeInAnimation() {
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('fade-in');
        }, index * 100);
    });
}

/**
 * Show loading spinner
 */
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `
            <div class="text-center p-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-3 text-muted">Chargement...</p>
            </div>
        `;
    }
}

/**
 * Show error message
 */
function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle"></i>
                <strong>Erreur :</strong> ${message}
            </div>
        `;
    }
}

/**
 * Show success message
 */
function showSuccess(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `
            <div class="alert alert-success" role="alert">
                <i class="bi bi-check-circle"></i>
                <strong>Succès :</strong> ${message}
            </div>
        `;
    }
}

/**
 * Format date to French locale
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

/**
 * Format file size
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

/**
 * Copy text to clipboard
 */
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showToast('Copié dans le presse-papiers', 'success');
    }).catch(err => {
        console.error('Erreur de copie:', err);
        showToast('Erreur lors de la copie', 'danger');
    });
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toastHtml = `
        <div class="toast align-items-center text-white bg-${type} border-0" role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    const toastContainer = document.createElement('div');
    toastContainer.innerHTML = toastHtml;
    document.body.appendChild(toastContainer);

    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        toastContainer.remove();
    });
}

/**
 * Confirm action with modal
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/**
 * Debounce function for search inputs
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Validate form before submit
 */
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return false;
        }
        return true;
    }
    return false;
}

/**
 * Get status badge HTML
 */
function getStatusBadge(status) {
    const badges = {
        'PENDING': '<span class="badge bg-warning"><i class="bi bi-hourglass"></i> En attente</span>',
        'PROCESSING': '<span class="badge bg-primary"><i class="bi bi-arrow-repeat"></i> En cours</span>',
        'SUCCESS': '<span class="badge bg-success"><i class="bi bi-check-circle"></i> Réussi</span>',
        'FAILED': '<span class="badge bg-danger"><i class="bi bi-x-circle"></i> Échoué</span>'
    };
    return badges[status] || '<span class="badge bg-secondary">Inconnu</span>';
}

/**
 * Get file type icon
 */
function getFileTypeIcon(type) {
    const icons = {
        'JSON': '<i class="bi bi-filetype-json text-primary"></i>',
        'XML': '<i class="bi bi-filetype-xml text-success"></i>',
        'TXT': '<i class="bi bi-filetype-txt text-warning"></i>'
    };
    return icons[type] || '<i class="bi bi-file-earmark"></i>';
}

/**
 * Refresh page data (for auto-refresh)
 */
function setupAutoRefresh(interval = 30000) {
    setInterval(() => {
        if (document.hasFocus()) {
            location.reload();
        }
    }, interval);
}

/**
 * Export table to CSV
 */
function exportTableToCSV(tableId, filename = 'export.csv') {
    const table = document.getElementById(tableId);
    if (!table) {
        console.error('Table not found');
        return;
    }

    let csv = [];
    const rows = table.querySelectorAll('tr');

    rows.forEach(row => {
        const cols = row.querySelectorAll('td, th');
        const csvRow = [];
        cols.forEach(col => {
            csvRow.push('"' + col.textContent.trim().replace(/"/g, '""') + '"');
        });
        csv.push(csvRow.join(','));
    });

    downloadCSV(csv.join('\n'), filename);
}

/**
 * Download CSV file
 */
function downloadCSV(csvContent, filename) {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/**
 * Highlight search terms in text
 */
function highlightSearchTerms(text, searchTerm) {
    if (!searchTerm) return text;
    const regex = new RegExp(`(${searchTerm})`, 'gi');
    return text.replace(regex, '<mark>$1</mark>');
}

/**
 * Scroll to top smoothly
 */
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Add scroll to top button
window.addEventListener('scroll', function() {
    if (window.scrollY > 300) {
        if (!document.getElementById('scrollTopBtn')) {
            const btn = document.createElement('button');
            btn.id = 'scrollTopBtn';
            btn.className = 'btn btn-primary';
            btn.style.cssText = 'position: fixed; bottom: 20px; right: 20px; z-index: 1000; border-radius: 50%; width: 50px; height: 50px;';
            btn.innerHTML = '<i class="bi bi-arrow-up"></i>';
            btn.onclick = scrollToTop;
            document.body.appendChild(btn);
        }
    } else {
        const btn = document.getElementById('scrollTopBtn');
        if (btn) btn.remove();
    }
});

// Log errors to console
window.addEventListener('error', function(e) {
    console.error('Error:', e.error);
});

// Export functions for global use
window.FileGenerator = {
    showLoading,
    showError,
    showSuccess,
    formatDate,
    formatFileSize,
    copyToClipboard,
    showToast,
    confirmAction,
    debounce,
    validateForm,
    getStatusBadge,
    getFileTypeIcon,
    setupAutoRefresh,
    exportTableToCSV,
    highlightSearchTerms,
    scrollToTop
};

console.log('File Generator utilities loaded successfully');

