import { useState } from "react";

const UploadImage = () => {
    const [file, setFile] = useState(null);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) {
            alert("Please select a file first.");
            return;
        }

        const formData = new FormData();
        formData.append("image", file);

        try {
            const response = await fetch("http://localhost:8080/api/v1/uploadImage", {
                method: "POST",
                body: formData,
            });

            const data = await response.text();
            console.log("Output:", data);

            alert("File uploaded successfully!");
        } catch (error) {
            console.error("Upload error:", error);
            alert("Failed to upload file.");
        }
    };

    return (
        <div className="upload-container">
            <label htmlFor="fileInput">Image:</label>
            <input
                type="file"
                id="fileInput"
                accept="image/*"
                onChange={handleFileChange}
            />
            <button onClick={handleUpload}>Upload</button>
        </div>
    );
};

export default UploadImage;
