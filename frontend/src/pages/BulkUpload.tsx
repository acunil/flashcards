import { useNavigate } from "react-router-dom";
import Header from "../components/header";
import { CaretLeft, DownloadSimple } from "phosphor-react";
import React from "react";

const sampleCsv = `front,back,frontHint,backHint,decks
Hello,Hallo,Greeting word,Used when meeting someone,greetings
Thank you,Danke,Expression of gratitude,
`;

const BulkUpload = () => {
  const navigate = useNavigate();

  const downloadSample = () => {
    const blob = new Blob([sampleCsv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "sample_cards.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const text = event.target?.result;
      console.log("CSV content:", text);
      // TODO: parse CSV and send to backend
    };
    reader.readAsText(file);
  };

  return (
    <div className="bg-green-200 min-h-screen">
      <Header isHomePage={true} />
      <div className="flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
          {/* Header with Back Button */}
          <div className="relative flex items-center h-12 mb-4">
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/")}
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <h1 className="text-xl font-bold text-center mx-auto">
              Upload Cards
            </h1>
          </div>
          {/* Instructions */}
          <p className="text-md mb-2">
            Cards can be uploaded in bulk using a CSV file.
          </p>
          <ul className="list-disc list-inside mb-4 text-sm">
            <li>
              Columns: <code>front</code>, <code>back</code>,{" "}
              <code>frontHint</code>, <code>backHint</code>, <code>decks</code>
            </li>
            <li>
              <code>decks</code> can contain multiple decks separated by commas
            </li>
            <li>Each row represents a single card</li>
          </ul>
          {/* Download Sample Button */}
          <button
            onClick={downloadSample}
            className={`relative flex bg-sky-200 p-2 mb-4 items-center justify-between text-black rounded shadow-lg cursor-pointer hover:bg-gray-200 border-black border-2 `}
          >
            <DownloadSimple
              size={20}
              className="ml-auto mr-1"
              weight="regular"
            />
            <span className="text-sm">Download Sample</span>
          </button>

          {/* File Upload Input */}
          <div>
            <input
              type="file"
              accept=".csv"
              onChange={handleFileUpload}
              className="border-2 p-2 rounded w-full"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default BulkUpload;
