(function() {
    const form    = document.getElementById('uploadForm');
    const message = document.getElementById('message');
  
    form.addEventListener('submit', async e => {
      e.preventDefault();
      message.textContent = '';
      message.className = '';
  
      const data = new FormData(form);
      try {
        const resp = await fetch(form.action, {
          method: form.method,
          body: data
        });
  
        if (!resp.ok) {
          let errText = `Upload failed (${resp.status})`;
          try {
            const errJson = await resp.json();
            if (errJson.message) errText += `: ${errJson.message}`;
          } catch (_) {}
          throw new Error(errText);
        }
  
        message.textContent = '✅ Upload succeeded!';
        message.classList.add('success');
        form.reset();
  
      } catch (err) {
        message.textContent = `❌ ${err.message}`;
        message.classList.add('error');
      }
    });
  })();
  