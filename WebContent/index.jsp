<!DOCTYPE html>
<html>
<head>
    <title>Upload Quiz Excel</title>
</head>
<body>
    <h2>Upload Excel File (.xlsx)</h2>
    <form method="post" action="upload" enctype="multipart/form-data">
        <input type="file" name="file" accept=".xlsx" required>
        <button type="submit">Upload and Start Quiz</button>
    </form>
</body>
</html>
