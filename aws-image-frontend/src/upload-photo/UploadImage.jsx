import { useState } from "react";
import { Button, Box, InputLabel, Input } from "@mui/material";

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
        <Box
            sx={{
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                gap: 2,
                maxWidth: 1000,
                marginLeft: 20,
                marginTop: 10,
                marginBottom: 5
            }}
        >
            <InputLabel htmlFor="fileInput" sx={{ marginRight: 2 }}>
                Image:
            </InputLabel>
            <Input
                type="file"
                id="fileInput"
                accept="image/*"
                onChange={handleFileChange}
                sx={{ marginRight: 2 }}
            />
            <Button
                variant="contained"
                color="primary"
                onClick={handleUpload}
                sx={{
                    borderRadius: 2,
                    padding: "10px 20px",
                    fontSize: "16px",
                    backgroundColor: "#1976d2",
                    ":hover": {
                        backgroundColor: "#1565c0",
                    },
                }}
            >
                Upload
            </Button>
        </Box>
    );
};

export default UploadImage;
