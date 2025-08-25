import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../../contexts";
import Header from "../header";
import { SmileyXEyes } from "phosphor-react";

const LandingWrapper = () => {
  const navigate = useNavigate();
  const { subjects, loading, error } = useAppContext();

  useEffect(() => {
    if (loading) return; // don’t redirect while still loading

    if (subjects.length === 0) {
      navigate("/subjects");
    } else {
      navigate("/home");
    }
  }, [subjects, loading, navigate]);

  if (error) {
    return (
      <div className="bg-green-200 min-h-screen">
        <Header isErrorMode={true} />
        <div className="flex justify-center">
          <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
            {/* Header with Back Button */}
            <div className="relative flex items-center h-12 mb-4">
              <h1 className="text-xl font-bold text-center mx-auto">
                <div className="flex flex-row gap-2 items-center">
                  <SmileyXEyes size={30} weight="bold" />
                  Something's gone wrong
                </div>
              </h1>
            </div>
            <p className="text-md mb-2 text-center">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  if (loading)
    return (
      <div className="fixed inset-0 bg-green-200 flex items-center justify-center z-50">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-black border-t-transparent"></div>
      </div>
    );

  return null;
};

export default LandingWrapper;
